package com.wissensalt.dto;

public record UpdateTodoRequest(Long id, String task, boolean completed) {

}