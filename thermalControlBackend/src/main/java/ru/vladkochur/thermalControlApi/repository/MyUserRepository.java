package ru.vladkochur.thermalControlApi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.vladkochur.thermalControlApi.entity.MyUser;

import java.util.List;
import java.util.Optional;

public interface MyUserRepository extends JpaRepository<MyUser, Integer> {
    Optional<MyUser> findByLogin(String login);
    Optional<MyUser> findByTelegram(String telegramId);
    @Query(value = "from MyUser order by case when roles = 'ROLE_SENSOR' then 1 else 2 end")
    List<MyUser> findAllSensorsFirst();
}
