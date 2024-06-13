package ru.vladkochur.thermalControlApi.service.securtyService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vladkochur.thermalControlApi.configuration.securityConfig.MyUserDetails;
import ru.vladkochur.thermalControlApi.repository.MyUserRepository;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private MyUserRepository myUserRepository;

    @Autowired
    public void setUserRepository(MyUserRepository myUserRepository) {
        this.myUserRepository = myUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return myUserRepository.findByLogin(username).map(MyUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Login " + username + " is not found"));
    }

}
