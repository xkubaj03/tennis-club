package com.inqool.tennisclub.rest;

import com.inqool.tennisclub.api.auth.LoginDto;
import com.inqool.tennisclub.api.auth.ResponseDto;
import com.inqool.tennisclub.facade.UserFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tags(value = @Tag(name = "Authentication API"))
@RestController
@RequestMapping("/api/auth")
public class LoginRestController {
    private final UserFacade userFacade;

    @Autowired
    public LoginRestController(UserFacade userFacade) {
        this.userFacade = userFacade;
    }

    @Operation(summary = "Authenticate yourself")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully authenticated. Returning token."),
        @ApiResponse(
                responseCode = "400",
                description = "Invalid data provided",
                content = @Content) // TODO wrong password...
    })
    @PostMapping("/login")
    public ResponseEntity<ResponseDto> login(@RequestBody @Valid LoginDto dto) {
        ResponseDto token = userFacade.login(dto);
        return ResponseEntity.ok(token);
    }
}
