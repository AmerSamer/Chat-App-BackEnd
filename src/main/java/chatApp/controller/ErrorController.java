package chatApp.controller;


import chatApp.customEntities.CustomResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/error")
public class ErrorController {

    @RequestMapping(method = RequestMethod.GET)
    private ResponseEntity<CustomResponse<String>> error() {
        CustomResponse<String> response = new CustomResponse<>("error", "error");
        return ResponseEntity.badRequest().body(response);
    }


}
