package com.billetterie.billetterie.controller;

import com.billetterie.billetterie.dto.ResponseDto;
import com.billetterie.billetterie.service.CategorieService;
import com.billetterie.billetterie.dto.CategorieRequestDto;
import com.billetterie.billetterie.service.SalleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categorie")
public class CategorieController {
    private final CategorieService categorieService;
    @GetMapping
    public List<ResponseDto> getAll() {
        return categorieService.getAll();
    }
    @PostMapping()
    private ResponseDto create(@RequestBody CategorieRequestDto categorieRequest) {
        return  categorieService.create(categorieRequest);
    }
    @GetMapping("/{id}")
    public ResponseDto getById(@PathVariable Long id) throws Throwable {
        return categorieService.getById(id);
    }
    @DeleteMapping("/{id}")
    public ResponseDto delete(@PathVariable Long id) throws Throwable {
        return categorieService.delete(id);
    }
    @PutMapping("/{id}")
    public ResponseDto update(@PathVariable Long id, @RequestBody CategorieRequestDto categorieRequest) {
        return categorieService.update(id,categorieRequest);
    }

}
