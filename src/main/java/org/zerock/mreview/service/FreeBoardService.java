package org.zerock.mreview.service;

import org.zerock.mreview.dto.FreeBoardDTO;
import org.zerock.mreview.dto.PageRequestDTO;
import org.zerock.mreview.dto.PageResultDTO;
import org.zerock.mreview.entity.FreeBoard;

public interface FreeBoardService {

    Long register(FreeBoardDTO dto);

    PageResultDTO<FreeBoardDTO, FreeBoard> getList(PageRequestDTO requestDTO);

    FreeBoardDTO read(Long gno);

    void modify(FreeBoardDTO dto);

    void remove(Long gno);

    default FreeBoard dtoToEntity(FreeBoardDTO dto) {
        FreeBoard entity = FreeBoard.builder()
                .gno(dto.getGno())
                .title(dto.getTitle())
                .content(dto.getContent())
                .writer(dto.getWriter())
                .build();
        return entity;
    }

    default FreeBoardDTO entityToDto(FreeBoard entity){

        FreeBoardDTO dto  = FreeBoardDTO.builder()
                .gno(entity.getGno())
                .title(entity.getTitle())
                .content(entity.getContent())
                .writer(entity.getWriter())
                .regDate(entity.getRegDate())
                .modDate(entity.getModDate())
                .build();
        return dto;
    }
}
