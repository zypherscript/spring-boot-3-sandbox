package com.example.demo.entity;

import org.springframework.data.annotation.Id;

public record Customer(@Id Integer id, String name) {

}
