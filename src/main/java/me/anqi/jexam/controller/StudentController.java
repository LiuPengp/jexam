package me.anqi.jexam.controller;

import lombok.extern.slf4j.Slf4j;
import me.anqi.jexam.entity.Exercise;
import me.anqi.jexam.entity.auxiliary.PaperFront;
import me.anqi.jexam.entity.auxiliary.UserAuxiliary;
import me.anqi.jexam.service.ExerciseService;
import me.anqi.jexam.service.PaperService;
import me.anqi.jexam.service.UserService;
import me.anqi.jexam.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author flyleft
 * @date 2018/4/8
 */
@Controller
@RequestMapping("/user/stu")
@Slf4j
public class StudentController {

    private UserService userService;

    private ExerciseService exerciseService;

    private PaperService paperService;

    @Autowired
    public StudentController(UserService userService, ExerciseService exerciseService, PaperService paperService) {
        this.userService = userService;
        this.exerciseService = exerciseService;
        this.paperService = paperService;
    }

    @GetMapping("/exams")
    public String exams(HttpServletRequest request, Model model) {
        UserAuxiliary userAuxiliary = RequestUtils.getUserAuxiliaryFromReq(request);
        List<PaperFront> paperFronts = userService.getPapersByStudentId(userAuxiliary.getId());
        model.addAttribute("papers", paperFronts);
        return "stu/exam_page";
    }

    @GetMapping("/exercises")
    public String exercises(HttpServletRequest request, Model model) {
        UserAuxiliary userAuxiliary = RequestUtils.getUserAuxiliaryFromReq(request);
        Set<Exercise> exerciseList = userService.getCollectionExercises(userAuxiliary.getId());
        model.addAttribute("exercises", exerciseList);
        return "stu/exercise_page";
    }

    @GetMapping("/collect_exercises/add/{id}")
    public String addCollectionExercises(@PathVariable Long id, HttpServletRequest request) {
        UserAuxiliary userAuxiliary = RequestUtils.getUserAuxiliaryFromReq(request);
        userService.addCollectionExercises(userAuxiliary.getId(), id);
        return "redirect:/exercises/list?type=all";
    }

    @GetMapping("/papers/{id}/join")
    public String joinExam(@PathVariable("id") Long paperId, Model model) {
        List<Exercise> exerciseList = exerciseService.getAllExercisesByPaperId(paperId);
        int time = paperService.findPageById(paperId).getAnswerTime();
        model.addAttribute("time", time);
        model.addAttribute("exercises", exerciseList);
        return "stu/answer_paper";
    }


}
