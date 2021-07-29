//package com.skillbox.blogengine.controllers;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.jayway.jsonpath.DocumentContext;
//import com.jayway.jsonpath.JsonPath;
//import main.controllers.exception.BadRequestException;
//import main.controllers.exception.EntityNotFoundException;
//import main.dto.ActionModel;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MvcResult;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//public class ActionControllerRestTest extends main.controllers.AbstractIntegrationTest {
//
//    private final String datetimeFormat = "yyyy-MM-dd'T'HH:mm";
//
//    @Test
//    void postOneActionTest() throws Exception {
//        String todo = "some action";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimeFormat);
//        LocalDateTime created = LocalDateTime.now().minusHours(1);
//        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
//        String createdFormatted = created.format(formatter);
//        String deadlineFormatted = deadline.format(formatter);
//
//        String actionJson = getActionJson(todo, created, deadline);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$['toDo']").value(todo))
//                .andExpect(jsonPath("$['created']").value(createdFormatted))
//                .andExpect(jsonPath("$['deadline']").value(deadlineFormatted));
//    }
//
////    @Test
////    void postManyActionsTest() throws Exception {
////        String jsonWithActions = "{\"actions\": [\n" +
////                "    {\n" +
////                "        \"toDo\": \"go school\",\n" +
////                "        \"created\": \"2020-08-23T14:58:52.591242\",\n" +
////                "        \"deadline\": \"2020-08-23T14:58:52.591267\"\n" +
////                "    },\n" +
////                "    {\n" +
////                "        \"toDo\": \"go work\",\n" +
////                "        \"created\": \"2020-08-23T14:58:52.591301\",\n" +
////                "        \"deadline\": \"2020-08-23T14:58:52.59131\"\n" +
////                "    },\n" +
////                "    {\n" +
////                "        \"toDo\": \"to do tests\",\n" +
////                "        \"created\": \"2020-08-23T14:58:52.591319\",\n" +
////                "        \"deadline\": \"2020-08-23T14:58:52.591325\"\n" +
////                "    }\n" +
////                "]}";
////
////
////
////        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
////                .contentType(MediaType.APPLICATION_JSON)
////                .content(jsonWithActions))
////                .andDo(print())
////                .andExpect(status().isOk())
////                .andExpect(jsonPath("$.actions.[1].toDo").value("go work"));
////    }
//
//    @Test
//    void getAllActionsTest() throws Exception {
//        String todo = "some action";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimeFormat);
//        LocalDateTime created = LocalDateTime.now().minusHours(1);
//        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
//        String createdFormatted = created.format(formatter);
//        String deadlineFormatted = deadline.format(formatter);
//
//        String actionJson = getActionJson(todo, created, deadline);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/api/todo"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0]['toDo']").value(todo))
//                .andExpect(jsonPath("$[0]['created']").value(createdFormatted))
//                .andExpect(jsonPath("$[0]['deadline']").value(deadlineFormatted));
//    }
//
//    @Test
//    void getWrongIndexOfActionTest() throws Exception {
//        mockMvc.perform(get("/api/todo/0"))
//                .andExpect(status().isNotFound())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EntityNotFoundException))
//                .andExpect(result -> assertEquals("Wrong index: 0", result.getResolvedException().getMessage()));
//    }
//
//    @Test
//    void putOneActionTest() throws Exception {
//        String actionJson1 = getActionJson("action", LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
//
//        String todo = "new action";
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimeFormat);
//        LocalDateTime created = LocalDateTime.now().minusDays(1);
//        LocalDateTime deadline = LocalDateTime.now().plusDays(2);
//        String createdFormatted = created.format(formatter);
//        String deadlineFormatted = deadline.format(formatter);
//
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson1))
//                .andExpect(status().isOk())
//                .andReturn();
//        String result = mvcResult.getResponse().getContentAsString();
//        DocumentContext documentContextResult = JsonPath.parse(result);
//
//        String actionJson2 = getActionJson(documentContextResult.read("$['id']"), todo, created, deadline);
//        System.out.println("!!!");
//        System.out.println(actionJson2);
//
//        mockMvc.perform(MockMvcRequestBuilders.put(String.format("/api/todo/%d", (int) documentContextResult.read("$['id']")))
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson2))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$['toDo']").value(todo))
//                .andExpect(jsonPath("$['created']").value(createdFormatted))
//                .andExpect(jsonPath("$['deadline']").value(deadlineFormatted));
//    }
//
//    @Test
//    void putWrongNumberOfActionTest() throws Exception {
//        String actionJson = getActionJson(0, "new action", LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/todo/1")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isBadRequest())
//                .andDo(print())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
//                .andExpect(result -> assertEquals("Can't change action", result.getResolvedException().getMessage()));
//    }
//
//    @Test
//    void putManyActionsTest() throws Exception {
//        String actionJson1 = getActionJson("action", LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
//
//        List<String> todo = List.of("new action1", "new action2");
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(datetimeFormat);
//        List<LocalDateTime> created = List.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
//        List<LocalDateTime> deadline = List.of(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(2));
//        List<String> createdFormatted = List.of(created.get(0).format(formatter), created.get(1).format(formatter));
//        List<String> deadlineFormatted = List.of(deadline.get(0).format(formatter), deadline.get(1).format(formatter));
//
//        String actionJson2 = getActionsListJson(todo, created, deadline);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson1))
//                .andExpect(status().isOk());
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson1))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson2))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0]['toDo']").value(todo.get(0)))
//                .andExpect(jsonPath("$[0]['created']").value(createdFormatted.get(0)))
//                .andExpect(jsonPath("$[0]['deadline']").value(deadlineFormatted.get(0)))
//
//                .andExpect(jsonPath("$[1]['toDo']").value(todo.get(1)))
//                .andExpect(jsonPath("$[1]['created']").value(createdFormatted.get(1)))
//                .andExpect(jsonPath("$[1]['deadline']").value(deadlineFormatted.get(1)))
//        ;
//    }
//
//    @Test
//    void putWrongNumberActionsTest() throws Exception {
//        String actionsListJsonJson = getActionsListJson(List.of("action1", "action2"),
//                List.of(LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1)),
//                List.of(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(1)));
//
//        mockMvc.perform(MockMvcRequestBuilders.put("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionsListJsonJson))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
//                .andExpect(result -> assertEquals("Can't change actions", result.getResolvedException().getMessage()));
//    }
//
//    @Test
//    void deleteOneActionTest() throws Exception {
//        String actionJson = getActionJson("action", LocalDateTime.now().minusHours(1), LocalDateTime.now().plusHours(1));
//
//        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isOk())
//                .andReturn();
//        String result = mvcResult.getResponse().getContentAsString();
//        DocumentContext documentContext = JsonPath.parse(result);
//
//        mockMvc.perform(MockMvcRequestBuilders.delete(String.format("/api/todo/%d", (int) documentContext.read("['id']"))))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/api/todo"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isEmpty());
//    }
//
//    @Test
//    void deleteWrongIndexOfActionTest() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/0"))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof BadRequestException))
//                .andExpect(result -> assertEquals("Can't delete action", result.getResolvedException().getMessage()));
//    }
//
//    @Test
//    void deleteAllActionsTest() throws Exception {
//        String actionJson = getActionJson("action", LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isOk());
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo"))
//                .andExpect(status().isOk());
//
//        mockMvc.perform(get("/api/todo"))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andExpect(jsonPath("$").isEmpty());
//    }
//
//    @Test
//    void postInvalidToDoParameterTest() throws Exception {
//        String todo = "";
//        LocalDateTime created = LocalDateTime.now().minusHours(1);
//        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
//
//        String actionJson = getActionJson(todo, created, deadline);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
//                        org.springframework.web.bind.MethodArgumentNotValidException));
//    }
//
//    @Test
//    void postInvalidCreatedParameterTest() throws Exception {
//        String todo = "some action";
//        LocalDateTime created = LocalDateTime.now().plusHours(1);
//        LocalDateTime deadline = LocalDateTime.now().plusHours(1);
//
//        String actionJson = getActionJson(todo, created, deadline);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
//                        org.springframework.web.bind.MethodArgumentNotValidException));
//    }
//
//    @Test
//    void postInvalidDeadlineParameterTest() throws Exception {
//        String todo = "some action";
//        LocalDateTime created = LocalDateTime.now().minusHours(1);
//        LocalDateTime deadline = LocalDateTime.now().minusHours(1);
//
//        String actionJson = getActionJson(todo, created, deadline);
//
//        mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(actionJson))
//                .andExpect(status().isBadRequest())
//                .andExpect(result -> assertTrue(result.getResolvedException() instanceof
//                        org.springframework.web.bind.MethodArgumentNotValidException));
//    }
//
//    private String getActionJson(String toDo, LocalDateTime created, LocalDateTime deadline) throws JsonProcessingException {
//        return getActionJson(null, toDo, created, deadline);
//    }
//
//    private String getActionJson(Integer id, String toDo, LocalDateTime created, LocalDateTime deadline) throws JsonProcessingException {
//        ActionModel action = new ActionModel();
//        action.setId(id);
//        action.setToDo(toDo);
//        action.setCreated(created);
//        action.setDeadline(deadline);
//        return mapper.writeValueAsString(action);
//    }
//
//    private String getActionsListJson(List<String> toDoList, List<LocalDateTime> createdList, List<LocalDateTime> deadlineList) throws JsonProcessingException {
//        if (toDoList.size() == createdList.size() && toDoList.size() == deadlineList.size()) {
//            List<ActionModel> actions = new ArrayList<>();
//            for (int i = 0; i < toDoList.size(); i++) {
//                ActionModel action = new ActionModel();
//                action.setToDo(toDoList.get(i));
//                action.setCreated(createdList.get(i));
//                action.setDeadline(deadlineList.get(i));
//                actions.add(action);
//            }
//            return mapper.writeValueAsString(actions);
//        }
//        return "";
//    }
//}