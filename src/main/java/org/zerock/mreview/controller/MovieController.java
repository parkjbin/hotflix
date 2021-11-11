package org.zerock.mreview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.mreview.dto.MovieDTO;
import org.zerock.mreview.dto.PageRequestDTO;
import org.zerock.mreview.entity.Movie;
import org.zerock.mreview.service.MovieService;

@Controller
@RequestMapping("/movie")
@Log4j2
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService; //final

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/register")
    public void register() {
        log.info("register..........");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public String register(MovieDTO movieDTO, RedirectAttributes redirectAttributes) {
        log.info("movieDTO: " + movieDTO);
        
        // 영화 객체와 영화 이미지 List<> 객체를 DB에 저장 후 영화 번호만 가져옴
        Long mno = movieService.register(movieDTO);

        // addAttribute로 전달한 값은 url뒤에 붙으며, 리프레시(새로고침)해도 데이터가 유지
        // addFlashAttribute로 전달한 값은 url뒤에 붙지 않는다. 일회성이라 리프레시할 경우 데이터가 소멸한다.
        // 또한 2개이상 쓸 경우, 데이터는 소멸한다. 따라서 맵을 이용하여 한번에 값을 전달해야한다.
        redirectAttributes.addFlashAttribute("msg", mno);

        // 영화와 영화 이미지들을 DB에 등록 후 목록 페이지로 이동
        return "redirect:/movie/list";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model) {
        log.info("pageRequestDTO: " + pageRequestDTO);
        model.addAttribute("result", movieService.getList(pageRequestDTO));
    }

    // @ModelAttribute
    // 1) @ModelAttribute 어노테이션이 붙은 객체를 자동으로 생성한다.
    // 2) 생성된 오브젝트에(PageRequestDTO) HTTP로 넘어 온 값들을 자동으로 바인딩한다.
    // 3) 마지막으로 @ModelAttribute 어노테이션이 붙은 객체(PageRequestDTO 객체)가 자동으로 Model 객체에 추가되고 뷰단으로 전달된다.
    @PreAuthorize("permitAll()")
    @GetMapping("/read")
    public void read(long mno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model){
        log.info("mno: " + mno);
        MovieDTO movieDTO = movieService.getMovie(mno);
        model.addAttribute("dto", movieDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/modify")
    public void modify(long mno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model){
        log.info("mno: " + mno);
        MovieDTO movieDTO = movieService.getMovie(mno);
        model.addAttribute("dto", movieDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove") // 영화 삭제 기능
    public String remove(long mno, RedirectAttributes redirectAttributes) {
        log.info("mno: " + mno);
        movieService.removeWithReplise(mno);
        redirectAttributes.addFlashAttribute("msg", mno);
        return "redirect:/movie/list";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/modify") // 영화 수정 기능
    public String modify(MovieDTO dto, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes) {
        log.info("post modify..................");
        log.info("dto: " + dto);

        movieService.modify(dto);

        redirectAttributes.addAttribute("page", requestDTO.getPage());
        redirectAttributes.addAttribute("type", requestDTO.getType());
        redirectAttributes.addAttribute("keyword", requestDTO.getKeyword());

        redirectAttributes.addAttribute("mno", dto.getMno());

        return "redirect:/movie/read";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/signup")
    public void signup() {
        log.info("signup....");
    }

}
