package ru.vladkochur.thermalControlApi.util.testConstraints;

import lombok.Getter;

@Getter
public enum myUserControllerTestConstraints {
    USERS_LIST("""
            <h3>Список пользователей:</h3>
            <div>
               \s
                <a href="/api/v1/administration/users/3">sensor, ROLE_SENSOR</a>
            </div>
            <div>
                <a href="/api/v1/administration/users/1">user, ROLE_USER, Telegram : 156000</a>
               \s
            </div>
            <div>
                <a href="/api/v1/administration/users/2">admin, ROLE_ADMIN, Telegram : 158000</a>
               \s
            </div>"""),
    TELEGRAM_INTERACTION_LIST("""
            <h3>Список отправивших запрос на авторизацию в Telegram:</h3>
            <div>
                <a>Telegram id : 156000,    Telegram username : User,    time : 5/1/24, 1:01 AM</a>
               \s
            </div>"""),
    CLEAR_TELEGRAM_INTERACTION("""
            <form method="post" action="/api/v1/administration/users/interactions"><input type="hidden" name="_method" value="DELETE"/>
                <input type="submit" value="Очистить список желающих взаимодействия в Telegram"/>
            </form>"""),
    CREATE_USER("""
            <form method="GET" action="/api/v1/administration/users/new">
                <input type="submit" value="Добавить нового пользователя"/>
            </form>"""),
    EMPTY_LOGIN("""
            <label for="login">Логин: </label>
                <input type="text" id="login" name="login" value=""/>
                <div style="color:red">Необходимо указать логин</div>
                <br/>"""),
    NEW_USER("""
            <form method="POST" action="/api/v1/administration/users">
                <label for="login">Логин: </label>
                <input type="text" id="login" name="login" value=""/>
               \s
                <br/>
                <label for="password">Пароль: </label>
                <input type="text" id="password" name="password" value=""/>
               \s
                <br/>
                <label for="telegram">Telegram id: </label>
                <input type="text" id="telegram" name="telegram" value=""/>
               \s
                <br/>
                <label for="roles">Роли: </label>
                <input type="text" id="roles" name="roles" value=""/>
               \s
                <br/>
                <input type="submit" value="Добавить!"/>
            </form>"""),
    BACK_TO_ALL_USERS("""
            <form method="GET" action="/api/v1/administration/users">
                <input type="submit" value="Вернуться к списку пользователей"/>
            </form>"""),
    SHOW_USER("""
            <p>Логин: user</p>
            <br/>
            <p>Telegram ID: 156000</p>
            <br/>
            <p>Роли: ROLE_USER</p>"""),
    EDIT_USER("""
            <form method="GET" action="/api/v1/administration/users/1/edit">
                <input type="submit" value="Редактировать"/>
            </form>"""),
    EDIT_USER_PASSWORD("""
            <form method="GET" action="/api/v1/administration/users/1/edit_password">
                <input type="submit" value="Редактировать пароль"/>
            </form>"""),
    DELETE_USER("""
            <form method="post" action="/api/v1/administration/users/1"><input type="hidden" name="_method" value="DELETE"/>
                <input type="submit" value="Удалить"/>
            </form>"""),
    INCORRECT_NAME("""
                <label for="login">Логин: </label>
                <input type="text" id="login" name="login" value="Us"/>
                <div style="color:red">Логин должен состоять минимум из 3 и максимум из 100 символов</div>\
            """),
    EMPTY_PASSWORD("""
                <label for="password">Пароль: </label>
                <input type="text" id="password" name="password" value=""/>
                <div style="color:red">Необходимо указать пароль</div>\
            """),
    INCORRECT_PASSWORD("""
                <label for="password">Пароль: </label>
                <input type="text" id="password" name="password" value="Ps"/>
                <div style="color:red">Пароль должен состоять минимум из 3 и максимум из 100 символов</div>\
            """),
    DUPLICATE_TELEGRAM("""
                <label for="telegram">Telegram id: </label>
                <input type="text" id="telegram" name="telegram" value="156000"/>
                <div style="color:red">Этот telegram уже привязан к другому аккаунту</div>\
            """),
    DUPLICATE_LOGIN("""
                <label for="login">Логин: </label>
                <input type="text" id="login" name="login" value="user"/>
                <div style="color:red">Этот логин уже используется</div>\
            """),
    EMPTY_ROLES("""
                <label for="roles">Роли: </label>
                <input type="text" id="roles" name="roles" value=""/>
                <div style="color:red">Необходимо указать роли</div>\
            """),
    INCORRECT_ROLES("""
                <label for="roles">Роли: </label>
                <input type="text" id="roles" name="roles" value="INCORRECT_ROLE"/>
                <div style="color:red">Укажите корректные роли пользователя в формате : &quot;ROLE_USER&quot; или &quot;ROLE_USER, ROLE_ADMIN, ROLE_SENSOR&quot;</div>\
            """),
    SHOW_EDIT_USER("""
        <form method="post" action="/api/v1/administration/users/1"><input type="hidden" name="_method" value="PATCH"/>
            <label for="login">Логин: </label>
            <input type="text" id="login" name="login" value="user"/>
           \s
            <br/>
            <label for="telegram">Telegram id: </label>
            <input type="text" id="telegram" name="telegram" value="156000"/>
           \s
            <br/>
            <label for="roles">Роли: </label>
            <input type="text" id="roles" name="roles" value="ROLE_USER"/>
           \s
            <br/>

            <input type="hidden" id="password" name="password" value="123">

            <input type="submit" value="Обновить!"/>
        </form>"""),
SHOW_EDIT_USER_PASSWORD("""
        <form method="post" action="/api/v1/administration/users/1/edit_password"><input type="hidden" name="_method" value="PATCH"/>

            <input type="hidden" id="roles" name="roles" value="ROLE_USER">
            <input type="hidden" id="telegram" name="telegram" value="156000">
            <input type="hidden" id="login" name="login" value="user">
            <br><br>

            <label for="password">Новый пароль: </label>
            <input type="text"  name="password" id="password"/>
           \s
            <br/>
            <input type="submit" value="Обновить!"/>
        </form>""");


    private final String message;

    myUserControllerTestConstraints(String message) {
        this.message = message;
    }
}
