package com.example.pisioconf_backend.security.models;
import lombok.Data;

import java.util.List;

@Data
public class AuthorizationRules {
    List<Rule> rules;
}