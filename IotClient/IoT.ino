#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <Adafruit_NeoPixel.h>
#include <EEPROM.h>
#include <EncButton.h>
#include <DHT.h>
#include <ThreeWire.h>
#include <RtcDS1302.h>
#include <ArduinoJson.h>
#include <NTPClient.h>
#include <WiFiUdp.h>

#define RELAY_PIN 12                        //relay pin(esp native number)
#define BUTTON_PIN 13                       //button pin(esp native number)
#define DHTPIN 14                           //temperature & humidity sensor pin(esp native number)
#define LED_PIN 15                          //argb led pin(esp native number)
#define TIME_FOR_REQUEST_INTERACTION 1000   //time delay for interaction request in ms
#define TIME_FOR_REQUEST_REGISTRATION 3000  //time delay for registration request in ms
#define TEMPERATURE_DELTA 6                 //temperature regulation delta in Celsius

const char* ssid = " ";          //wifi 
const char* wifiPassword = " ";  //wifi password
const char* login = " ";         //module authorization
const char* password = " ";      //module authorization
const String baseUrl = " ";      //base url for all requests

Adafruit_NeoPixel strip(1, LED_PIN, NEO_GRB + NEO_KHZ800);
DHT dht(DHTPIN, DHT11);
ThreeWire myWire(4, 5, 2);
RtcDS1302<ThreeWire> Rtc(myWire);
Button btn(BUTTON_PIN);
WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "pool.ntp.org");
bool isSendingFailed = false;
unsigned long timer1;
unsigned long timer2;
unsigned long timer3;
unsigned long timer4;
float minTemp;
float optTemp;
short startAt;
short endAt;

void setup() {
  pinMode(RELAY_PIN, OUTPUT);     //set relay pin
  dht.begin();                    //measurements sensor init
  strip.begin();                  //argb led init
  strip.setBrightness(50);
  WiFi.begin(ssid, wifiPassword); //wifi init
  while (WiFi.status() != WL_CONNECTED) {
    ledMode(2);                   //wifi not ok led
    delay(1000);
  }
  ledMode(3);                     //wifi ok led
  timeClient.begin();             //time web server init
  timeClient.setTimeOffset(10800);//Msk zone
  timeClient.update();
  if (timeClient.getEpochTime() > 1638316800) { //check for absurde (01.01.2020)
    RtcDateTime rtcNow;
    rtcNow.InitWithUnix64Time(timeClient.getEpochTime()); //get actual Msk time from server
    Rtc.SetDateTime(rtcNow);                              //set time to rtc module
  }
}

void loop() {
  short minutes = Rtc.GetDateTime().Minute();
  short seconds = Rtc.GetDateTime().Second();
  short hours = Rtc.GetDateTime().Hour();
  short thisDayMinutes = hours * 60 + minutes;
  float temp = dht.readTemperature();

  if (millis() % 200 == 0) {  //every 0.2 second
    btnControl();
  }

  if (millis() - timer1 > 2000 && seconds == 30) {  //every 1 minute
    timer1 = millis();
    getSettings();
  }

  if (millis() - timer2 > 2000 && (minutes % 5 == 0 && seconds == 30)) {  //every 5 minutes
    timer2 = millis();
    checkPanicMeasurement(temp);
    thermalControl(temp, thisDayMinutes);
  }

  if (millis() - timer3 > 2000 && (((minutes == 0 || minutes == 30) && seconds == 30) || isSendingFailed)) {  //every half hour or if failed
    timer3 = millis();
    addMeasurement(temp);
  }

  if (millis() - timer4 > 2000 && hours == 0 && minutes == 0 && seconds == 30) {  //every day 
    timer4 = millis();
    startAt = 0;  //period start time clearing (offline sabotage)
    endAt = 1440; //period end time clearing (offline sabotage)
  }
}

void btnControl() { //button control for registration and interaction
  btn.tick();
  if (btn.release()) {
    int press_time = btn.pressFor();
    if (press_time > TIME_FOR_REQUEST_REGISTRATION) {
      requestRegistraction();
    } else if (press_time > TIME_FOR_REQUEST_INTERACTION) {
      requestInteraction();
    }
    btn.clear();
  }
}

void requestRegistraction() { //send registration request to server
  WiFiClient client;
  HTTPClient http;
  int httpCode;
  http.setAuthorization(login, password);
  JsonDocument doc;
  String json;
  doc["serial"] = atoi(login);
  serializeJson(doc, json);
  if (http.begin(client, baseUrl + "sensors/registration")) {
    http.addHeader("Content-Type", "application/json");
    httpCode = http.POST(json);
  }
  http.end();
  showHttpColor(httpCode);
}

void requestInteraction() { //send interaction request to server
  WiFiClient client;
  HTTPClient http;
  int httpCode;
  http.setAuthorization(login, password);
  JsonDocument doc;
  String json;
  doc["serial"] = atoi(login);
  serializeJson(doc, json);
  if (http.begin(client, baseUrl + "sensors/interaction")) {
    http.addHeader("Content-Type", "application/json");
    httpCode = http.POST(json);
  }
  http.end();
  showHttpColor(httpCode);
}

void addMeasurement(float temp) {  //send temperature and humidity to server
  WiFiClient client;
  HTTPClient http;
  int httpCode = 0;
  http.setAuthorization(login, password);
  JsonDocument doc;
  String json;
  doc["humidity"] = dht.readHumidity();
  doc["temperature"] = temp;
  JsonObject sensor = doc.createNestedObject("sensor");
  sensor["serial"] = atoi(login);
  serializeJson(doc, json);
  if (http.begin(client, baseUrl + "measurements/add")) {
    http.addHeader("Content-Type", "application/json");
    httpCode = http.POST(json);
  }
  http.end();
  showHttpColor(httpCode);
  if (httpCode == HTTP_CODE_OK) {
    isSendingFailed = false;
  } else {
    isSendingFailed = true;
  }
}

void checkPanicMeasurement(float temp) {  //check temperature and send warning to server if needed
  if (temp > 60 || temp <= 5) {
    WiFiClient client;
    HTTPClient http;
    int httpCode;
    http.setAuthorization(login, password);
    JsonDocument doc;
    String json;
    doc["humidity"] = dht.readHumidity();
    doc["temperature"] = temp;
    JsonObject sensor = doc.createNestedObject("sensor");
    sensor["serial"] = atoi(login);
    serializeJson(doc, json);
    if (http.begin(client, baseUrl + "measurements/panic")) {
      http.addHeader("Content-Type", "application/json");
      httpCode = http.POST(json);
    }
    http.end();
    showHttpColor(httpCode);
  }
}

void getSettings() {  //get settings data from server
  WiFiClient client;
  HTTPClient http;
  JsonDocument parsed;
  int httpCode;
  String payload;
  http.setAuthorization(login, password);
  if (http.begin(client, baseUrl + "sensors/" + login)) {
    httpCode = http.GET();
    if (httpCode == HTTP_CODE_OK) {
      payload = http.getString();
    }
  }
  http.end();
  showHttpColor(httpCode);
  DeserializationError error = deserializeJson(parsed, payload);
  if (error) {
    ledMode(0);
  } else {
    if (parsed["serial"] == atoi(login)) {
      minTemp = parsed["minimalTemperature"];
      optTemp = parsed["optimalTemperature"];
      startAt = parsed["startAt"];
      endAt = parsed["endAt"];
    } else {
      ledMode(0);
    }
  }
}

void thermalControl(float temp, short thisDayMinutes) {
  if (thisDayMinutes < endAt && thisDayMinutes > startAt) {    //if it's optimal period time
    relayControl(temp, optTemp);
  } else {
    relayControl(temp, minTemp);
  }
}

void relayControl(float actualTemp, float neededTemp) {
  if (actualTemp <= 5) {                                              //unfrost protection
    digitalWrite(RELAY_PIN, LOW);
  } else if ((actualTemp - neededTemp) > (TEMPERATURE_DELTA / 2)) {  //if needed off
    digitalWrite(RELAY_PIN, HIGH);
  } else if ((neededTemp - actualTemp) > (TEMPERATURE_DELTA / 2)) {  //if needed on
    digitalWrite(RELAY_PIN, LOW);
  }
}

void showHttpColor(int httpCode) {
  if (httpCode == HTTP_CODE_OK) {
    ledMode(1);
  } else if (httpCode < 0) {
    ledMode(4);
  } else if (httpCode == HTTP_CODE_FORBIDDEN) {
    ledMode(5);
  } else if (httpCode >= 500) {
    ledMode(6);
  } else {
    ledMode(0);
  }
}

void ledMode(int mode) {
  switch (mode) {
    case 0:
      strip.setPixelColor(0, 255, 0, 0);  //red
      break;
    case 1:
      strip.setPixelColor(0, 0, 255, 0);  //green
      break;
    case 2:
      strip.setPixelColor(0, 255, 255, 0);  //yellow
      break;
    case 3:
      strip.setPixelColor(0, 0, 0, 255);  //blue
      break;
    case 4:
      strip.setPixelColor(0, 186, 85, 211);  //purple
      break;
    case 5:
      strip.setPixelColor(0, 255, 140, 0);  //orange
      break;
    case 6:
      strip.setPixelColor(0, 255, 255, 255);  //white
      break;
  }
  strip.show();
}