package org.zerock.mreview.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.mreview.dto.FreeBoardDTO;
import org.zerock.mreview.dto.PageRequestDTO;
import org.zerock.mreview.security.dto.ClubAuthMemberDTO;
import org.zerock.mreview.service.FreeBoardService;

@Controller
@RequestMapping("freeboard")
@Log4j2
@RequiredArgsConstructor //자동 주입을 위한 Annotation
public class FreeBoardController {

    private final FreeBoardService service; //final로 선언

//    @GetMapping("/")
//    public String index() {
//
//        return "redirect:/freeboard/list";
//    }

    @PreAuthorize("permitAll()")
    @GetMapping("list")
    public void list(PageRequestDTO pageRequestDTO, Model model){

        log.info("list............." + pageRequestDTO);

        model.addAttribute("result", service.getList(pageRequestDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("register")
    public void register(@AuthenticationPrincipal ClubAuthMemberDTO clubAuthMemberDTO){
        log.info("regiser get...");
        log.info("----------------------");
        log.info(clubAuthMemberDTO);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("register")
    public String registerPost(FreeBoardDTO dto, RedirectAttributes redirectAttributes){

        log.info("dto..." + dto);

        //새로 추가된 엔티티의 번호
        Long gno = service.register(dto);

        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/freeboard/list";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("read")
    public void read(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model ){
        log.info("gno: " + gno);
        FreeBoardDTO dto = service.read(gno);
        model.addAttribute("dto", dto);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping( "modify")
    public void modify(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model ){
        log.info("gno: " + gno);
        FreeBoardDTO dto = service.read(gno);
        model.addAttribute("dto", dto);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("remove")
    public String remove(long gno, RedirectAttributes redirectAttributes){


        log.info("gno: " + gno);

        service.remove(gno);

        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/freeboard/list";

    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping("modify")
    public String modify(FreeBoardDTO dto,
                         @ModelAttribute("requestDTO") PageRequestDTO requestDTO,
                         RedirectAttributes redirectAttributes){


        log.info("post modify.........................................");
        log.info("dto: " + dto);

        service.modify(dto);

        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());

        redirectAttributes.addAttribute("gno",dto.getGno());

        return "redirect:/freeboard/read";

    }



}
