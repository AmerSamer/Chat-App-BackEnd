package chatApp.controller;


import chatApp.customEntities.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ErrorController {

    @RequestMapping(method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<String>> errorGet() {
        CustomResponse<String> response = new CustomResponse<>("error", "error");
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping(method = RequestMethod.POST)
    private ResponseEntity<CustomResponse<String>> errorPost() {
        CustomResponse<String> response = new CustomResponse<>("error", "error");
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    private ResponseEntity<CustomResponse<String>> errorPatch() {
        CustomResponse<String> response = new CustomResponse<>("error", "error");
        return ResponseEntity.badRequest().body(response);
    }

    @RequestMapping(method = RequestMethod.PUT)
    private ResponseEntity<CustomResponse<String>> errorPut() {
        CustomResponse<String> response = new CustomResponse<>("error", "error");
        return ResponseEntity.badRequest().body(response);
    }


}
