package com.wissensalt.repository;

import com.wissensalt.model.Todo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TodoRepository extends JpaRepository<Todo, Long> {

  List<Todo> findByCompleted(boolean completed);
}
