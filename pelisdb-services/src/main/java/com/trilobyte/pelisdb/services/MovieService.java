package com.trilobyte.pelisdb.services;

import com.trilobyte.pelisdb.dto.MovieDto;
import com.trilobyte.pelisdb.dto.MovieReqDto;
import com.trilobyte.pelisdb.dto.MovieResDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface MovieService {

    MovieDto findById(@NotNull Long id);

    MovieResDto findAll(String title, Integer page, Integer size, String sort1, String sort2le);

    MovieDto update(@NotNull Long movieId, @NotNull @Valid MovieReqDto dto);

    MovieDto save(@NotNull @Valid MovieReqDto dto);

    void delete(@NotNull Long id);
}
