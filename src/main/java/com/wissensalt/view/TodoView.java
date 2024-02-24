package com.wissensalt.view;

import static com.vaadin.flow.theme.lumo.LumoUtility.Padding.Horizontal;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility.Border;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderColor;
import com.vaadin.flow.theme.lumo.LumoUtility.BorderRadius;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding.Vertical;
import com.wissensalt.model.Todo;
import com.wissensalt.repository.TodoRepository;
import java.util.ArrayList;
import java.util.List;


@Route("")
public class TodoView extends VerticalLayout {

  private final TodoRepository todoRepository;
  private VerticalLayout availableTodoLayout;
  private VerticalLayout completedTodoLayout;
  private final TextField inputTodo = new TextField();

  public TodoView(TodoRepository todoRepository) {
    this.todoRepository = todoRepository;

    setupComponents();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    inputTodo.focus();
  }

  private void setupComponents() {
    add(new H1("Vaadin Todo Application"));
    setAlignItems(Alignment.CENTER);
    final HorizontalLayout inputLayout = new HorizontalLayout();
    inputLayout.setAlignItems(Alignment.CENTER);
    inputLayout.add(new Text("What needs to be done"));
    inputTodo.addKeyPressListener(e -> {
      if (e.getKey().equals(Key.ENTER)) {
        final Todo newTodo = new Todo();
        newTodo.setTask(inputTodo.getValue());
        todoRepository.save(newTodo);
        final HorizontalLayout todoContainer = getTodoContainer();
        final TextField todoTextField = getTodoTextField(newTodo);
        final Checkbox todoCheckbox = getTodoCheckbox(newTodo, todoContainer);
        todoContainer.add(todoTextField);
        todoContainer.add(todoCheckbox);
        availableTodoLayout.add(todoContainer);
        inputTodo.setValue("");
        showNotification("New Todo " + inputTodo.getValue() + " has been added");
      }
    });
    inputLayout.add(inputTodo);
    add(inputLayout);
    availableTodoLayout = getTodos(false);
    completedTodoLayout = getTodos(true);
    TabSheet tabSheet = new TabSheet();
    tabSheet.setMinWidth(50, Unit.PERCENTAGE);
    tabSheet.add("Available", availableTodoLayout);
    tabSheet.add("Completed", completedTodoLayout);
    add(tabSheet);
  }

  private VerticalLayout getTodos(boolean completed) {
    final List<Todo> availableTodos = todoRepository.findByCompleted(completed);
    if (availableTodos == null) {
      return new VerticalLayout();
    }

    List<HorizontalLayout> todoContainers = new ArrayList<>();
    for (Todo todo : availableTodos) {
      final HorizontalLayout todoContainer = getTodoContainer();
      final TextField textField = getTodoTextField(todo);
      todoContainer.add(textField);
      final Checkbox checkbox = getTodoCheckbox(todo, todoContainer);
      todoContainer.add(checkbox);
      todoContainers.add(todoContainer);
    }

    final VerticalLayout layout = new VerticalLayout();
    layout.setSizeFull();
    for (HorizontalLayout todoContainer : todoContainers) {
      layout.add(todoContainer);
    }

    return layout;
  }

  private Checkbox getTodoCheckbox(Todo todo, HorizontalLayout todoContainer) {
    Checkbox checkbox = new Checkbox();
    checkbox.setValue(todo.isCompleted());
    checkbox.addValueChangeListener(e -> {
      boolean updated = e.getValue();
      todo.setCompleted(updated);
      todoRepository.save(todo);
      todoContainer.removeFromParent();
      if (updated) {
        completedTodoLayout.add(todoContainer);
        showNotification("Task " + todo.getTask() + " Moved to Completed");
      } else {
        availableTodoLayout.add(todoContainer);
        showNotification("Task " + todo.getTask() + " Moved to Available");
      }
    });
    return checkbox;
  }

  private TextField getTodoTextField(Todo todo) {
    final TextField textField = new TextField();
    textField.setWidthFull();
    textField.addKeyPressListener(e -> {
      if (e.getKey().matches(Key.ENTER.toString())) {
        final String oldValue = todo.getTask();
        final String newValue = textField.getValue();
        todo.setTask(newValue);
        todoRepository.save(todo);
        showNotification("Task " + oldValue + " Updated to: " + newValue);
      }
    });
    textField.setValue(todo.getTask());
    return textField;
  }

  private static HorizontalLayout getTodoContainer() {
    final HorizontalLayout todoContainer = new HorizontalLayout();
    todoContainer.setSizeFull();
    todoContainer.setJustifyContentMode(JustifyContentMode.BETWEEN);
    todoContainer.setAlignItems(Alignment.CENTER);
    todoContainer.addClassName(Border.ALL);
    todoContainer.addClassName(BorderColor.PRIMARY);
    todoContainer.addClassName(BorderRadius.SMALL);
    todoContainer.addClassNames(Vertical.SMALL, Horizontal.SMALL);

    return todoContainer;
  }

  private static void showNotification(String message) {
    Notification notification = new Notification();
    notification.setDuration(5000);
    final Icon icon = VaadinIcon.CHECK_CIRCLE.create();
    final Div info = new Div(new Text(message));
    final HorizontalLayout layout = new HorizontalLayout(icon, info);
    layout.setAlignItems(Alignment.CENTER);
    notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    notification.add(layout);
    notification.open();
  }
}
