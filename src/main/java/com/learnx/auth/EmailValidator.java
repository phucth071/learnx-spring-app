package com.learnx.auth;

import org.springframework.stereotype.Service;

import java.util.function.Predicate;

@Service
public class EmailValidator implements Predicate<String> {

    @Override
    public boolean test(String s) {
        String regex = "/^[\\w\\-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$/gm";
        return true;
    }
}
