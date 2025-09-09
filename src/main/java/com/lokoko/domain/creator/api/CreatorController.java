package com.lokoko.domain.creator.api;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "CREATOR")
@RestController
@RequestMapping("/api/creator")
@RequiredArgsConstructor
public class CreatorController {

}
