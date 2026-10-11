package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.exception.NotFoundException;
import com.billetterie.billetterie.service.CategorieService;
import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.service.SalleService;
import com.billetterie.billetterie.utils.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorie")
public class CategorieController {
    private final CategorieService categorieService;
    @GetMapping
    public Response<ResponseDto> getAll() {
        return new Response<>(categorieService.getAll());
    }
    @PostMapping()
    private Response<ResponseDto> create(@Valid @RequestBody CategorieRequestDto categorieRequest) {
        return  new Response<>(categorieService.create(categorieRequest));
    }
    @GetMapping("/{id}")
    public Response<ResponseDto> getById(@PathVariable Long id) throws Throwable {
        return new Response<>(categorieService.getById(id));
    }
    @DeleteMapping("/{id}")
    public Response<ResponseDto> delete(@PathVariable Long id) throws Throwable{
        return new Response<>(categorieService.delete(id));
    }
    @PutMapping("/{id}")
    public Response<ResponseDto> update(@PathVariable Long id,
                                        @Valid @RequestBody CategorieRequestDto categorieRequest){
        return new Response<>(categorieService.update(id,categorieRequest));
    }

}
