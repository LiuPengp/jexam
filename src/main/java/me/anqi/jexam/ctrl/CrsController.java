package me.anqi.jexam.ctrl;

import lombok.extern.slf4j.Slf4j;
import me.anqi.jexam.entity.*;
import me.anqi.jexam.entity.auxiliary.ChpForm;
import me.anqi.jexam.entity.auxiliary.ExeForm;
import me.anqi.jexam.exception.NoPageException;
import me.anqi.jexam.service.inter.CrsSer;
import me.anqi.jexam.service.inter.NoticeSer;
import me.anqi.jexam.utils.FileType;
import me.anqi.jexam.utils.FileUtils;
import me.jcala.jmooc.entity.*;
import me.anqi.jexam.utils.CommonUtils;
import me.anqi.jexam.utils.RequestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;

/**
 *  课程管理控制器
 */
@Controller
@Slf4j
public class CrsController {

    private CrsSer crsSer;
    private NoticeSer noticeSer;

    @Autowired
    public CrsController(CrsSer crsSer, NoticeSer noticeSer) {
        this.crsSer = crsSer;
        this.noticeSer = noticeSer;
    }


//-------------------------------------------------课程管理相关---------------------------------------------

    /**
     *  课程管理界面
     *  do为add返回添加课程页面
     *  do为md返回课程管理界面
     */
    @GetMapping("/user/tea/crs_mgr")
    public String courseManagerPage(@RequestParam("do") String operate,HttpServletRequest request,Model model){

        if (operate==null || CommonUtils.isEmpty(operate)){
            throw new NoPageException();
        }
        if ("add".equals(operate.trim())){
            return "mgr/crs_mgr_add";
        }else if ("mod".equals(operate.trim())){
            long userId=RequestUtils.getUserIdFromReq(request);
            Set<Course> courses=crsSer.getCourseList(userId);
            model.addAttribute("courses",courses);
            return "mgr/crs_mgr_mod";
        }else {
            throw new NoPageException();
        }

    }

    /**
     * 课程添加请求
     */
    @PostMapping("/user/tea/crs_mgr/add")
    public String addCourse(@ModelAttribute("course") @Valid Course course,
                            BindingResult result,
                            HttpServletRequest request){

        if (result.hasErrors()) {
            return "mgr/crs_mgr_add";
        }

        User user= RequestUtils.getUserFromReq(request);
        course.setUser(user);

        long crsId=crsSer.addCourse(course);
        return "redirect:/user/tea/chp_mgr?crs_id="+crsId;
    }

    /**
     * 课程删除请求
     */
    @GetMapping("/user/tea/crs_mgr/del")
    public String delCourse(@RequestParam("crs_id") long crsId){
        crsSer.delCourse(crsId);
        return "redirect:/user/tea/crs_mgr?do=md";
    }

    //-------------------------------------------------章节管理相关---------------------------------------------
    /**
     *  返回章节列表界面
     */
    @GetMapping("/user/tea/chp_mgr")
    public String chpMgr(@RequestParam("crs_id") long crsId,Model model){
        Set<Chapter> chapters=crsSer.getChapterList(crsId);
        if (chapters!=null){
            model.addAttribute("chps",chapters);
            model.addAttribute("crs",crsId);
            model.addAttribute("pos",chapters.size()+1);
        }
        return "mgr/chp_mgr";
    }


    /**
     * 添加章节操作
     */
    @PostMapping("/user/tea/chp_mgr/add")
    public String chpPost(@ModelAttribute("chapter") @Valid ChpForm chpForm, BindingResult result){
        if (result.hasErrors()) {
            throw new RuntimeException("表单数据不合法");
        }
        crsSer.addChapter(chpForm);
        return "redirect:/user/tea/chp_mgr?crs_id="+chpForm.getCrs_id();
    }

    //-------------------------------------------------课时管理相关---------------------------------------------

    /**
     * 返回课时列表界面
     */
    @GetMapping("/user/tea/les_mgr")
    public String lesMgr(@RequestParam("crs_id") long crsId,
                         @RequestParam("chp_id") long chpId,
                         Model model){
        Set<Lesson> lessons=crsSer.getLessonList(chpId,crsId);

        if (lessons!=null){
            model.addAttribute("les",lessons);
            model.addAttribute("crs_id",crsId);
            model.addAttribute("chp_id",chpId);
            model.addAttribute("pos",lessons.size()+1);
        }
       return "mgr/les_mgr";
    }

    /**
     * 添加课时
     */
    @PostMapping("/user/tea/les_mgr/add")
    public String lesPost(@ModelAttribute("lesson") @Valid Lesson lesson,
                          BindingResult result,
                          @RequestParam("crs_id") long crsId,
                          @RequestParam("chp_id") long chpId){

        if (result.hasErrors()) {
            throw new RuntimeException("表单数据不合法");
        }

        crsSer.addLesson(lesson,crsId,chpId);

        return "redirect:/user/tea/les_mgr?crs_id="+crsId+"&chp_id="+chpId;
    }

    /**
     * 修改课时的视频
     */
    @PostMapping("/user/tea/les_mgr/video")
    public String modifyVideo(@RequestParam("crs_id") long crsId,
                              @RequestParam("chp_id") long chpId,
                              @RequestParam("les_id") long lesId,
                              @RequestParam("video") MultipartFile file){


        String url = FileUtils.uploadVideo(file, FileType.VIDEO,crsId);
        if (url!=null){
            crsSer.updateLessonVideo(url,lesId);
        }

        return "redirect:/user/tea/les_mgr?crs_id="+crsId+"&chp_id="+chpId;
    }

    /**
     *  上传文件
     */
    @PostMapping("/user/tea/les_mgr/file")
    public String uploadFile(@RequestParam("crs_id") long crsId,
                             @RequestParam("chp_id") long chpId,
                             @RequestParam("les_id") long lesId,
                             @RequestParam("file") MultipartFile file){

      crsSer.uploadLessonFile(file,crsId,lesId);

      return "redirect:/user/tea/les_mgr?crs_id="+crsId+"&chp_id="+chpId;
    }

   /* @GetMapping("/user/tea/les_mgr/exe")
    public String exeMgr(@RequestParam("crs_id") long crsId,
                         @RequestParam("chp_id") long chpId,
                         @RequestParam("les_id") long lesId){

        return "mgr/exe_mgr";

    }*/


    /**
     * 习题管理页面
     */
    @GetMapping("/user/tea/les_mgr/exe")
    public String exeMgr(@RequestParam("crs_id") long crsId,
                         @RequestParam("chp_id") long chpId,
                         @RequestParam("les_id") long lesId,
                         Model model){

        /*List<Exercise> exercises=new ArrayList<>();
        Exercise exercise=new Exercise("明朝时期张居正改革的一条鞭法的主要思想是()",2,'B',5,"这是一道送分题","java");
        Map<Character,String> map=new HashMap<>();
        map.put('A',"面向过程");
        map.put('B',"万物皆数");
        exercise.setChooseList(map);
        exercises.add(exercise);
        exercises.add(exercise);*/

        Set<Exercise> exercises=crsSer.getExerciseByLesId(lesId);
        model.addAttribute("exe",exercises);
        model.addAttribute("crs_id",crsId);
        model.addAttribute("chp_id",chpId);
        model.addAttribute("les_id",lesId);
        return "mgr/exe_mgr";

    }


    /**
     * 导入习题
     */
    @PostMapping("/user/tea/les_mgr/exe/add")
    public String exePost(@ModelAttribute("exe") @Valid ExeForm exeForm,
                          BindingResult result,
                          @RequestParam("crs_id") long crsId,
                          @RequestParam("chp_id") long chpId,
                          @RequestParam("les_id") long lesId,
                          HttpServletRequest request){

        if (result.hasErrors()) {
            throw new RuntimeException("表单数据不合法");
        }
        long userId=RequestUtils.getUserIdFromReq(request);
       crsSer.addExercise(exeForm,lesId,userId);
        return "redirect:/user/tea/les_mgr/exe?crs_id="+crsId+"&chp_id="+chpId+"&les_id="+lesId;
    }

    /**
     * 批量导入习题
     */
    @PostMapping("/user/tea/les_mgr/exe/add_batch")
    public String exePostBatch(@RequestParam("json") String json,
                               @RequestParam("crs_id") long crsId,
                               @RequestParam("chp_id") long chpId,
                               @RequestParam("les_id") long lesId,
                               HttpServletRequest request){

        if (json.trim().isEmpty()) {
            throw new RuntimeException("表单数据不合法");
        }
        long userId=RequestUtils.getUserIdFromReq(request);
        crsSer.addExerciseBatch(json,lesId,userId);
        return "redirect:/user/tea/les_mgr/exe?crs_id="+crsId+"&chp_id="+chpId+"&les_id="+lesId;
    }

    //---------------------------------------其他-----------------------------------------------

    /**
     * 参与课程
     */
    @GetMapping("/user/all/crs/join/{id}")
    public String joinCrs(@PathVariable("id") long id,HttpServletRequest request){
        long userId=RequestUtils.getUserIdFromReq(request);

        if (userId>=0) {
            crsSer.joinCrs(id, userId);
        }
        return "redirect:/course/"+id;
    }

    /**
     * 发表课程留言
     */
    @PostMapping("/user/all/cmt/crs/add")
    public String crsCmtPost(@ModelAttribute("cmt") @Valid Notice notice,
                             BindingResult result,HttpServletRequest request){

        if (result.hasErrors()) {
            throw new RuntimeException("表单数据不合法");
        }
       noticeSer.addComment(request,notice,1);
       return "redirect:/course/"+notice.getFromInfoId();
    }

    /**
     * 发表习题留言
     */
    @PostMapping("/user/all/cmt/exe/add")
    public String exeCmtPost(@ModelAttribute("cmt") @Valid Notice notice,
                             BindingResult result,HttpServletRequest request){

        if (result.hasErrors()) {
            throw new RuntimeException("表单数据不合法");
        }
        noticeSer.addComment(request,notice,2);
        return "redirect:/exercise/"+notice.getFromInfoId();
    }

    @GetMapping("/user/all/exe/col/add/{id}")
    public String exeColAdd(@PathVariable("id") long exeId,HttpServletRequest request){
       long userId=RequestUtils.getUserIdFromReq(request);
        crsSer.addColExe(exeId,userId);
        return "redirect:/exercise/"+exeId;
    }

    /**
     * 消息中心
     */
    @GetMapping("/user/all/notice")
    public String noticeMgr(@RequestParam("r") String role,HttpServletRequest request,Model model){
        long userId=RequestUtils.getUserIdFromReq(request);
        crsSer.clearNoticeNum(userId);
        Set<Notice> notices=crsSer.getNoticeList(userId);
        model.addAttribute("notices",notices);
        if ("t".equals(role)) {
            return "mgr/tea_not_mgr";
        }else{
            return "mgr/stu_not_mgr";
        }
    }

    /**
     *  学生习题收藏列表
     */
    @GetMapping("/user/stu/exe/col")
    public String colExePage(HttpServletRequest request,Model model){
        long userId=RequestUtils.getUserIdFromReq(request);
        List<Exercise> exercises=crsSer.getColExeList(userId);
        model.addAttribute("exe",exercises);
        return "mgr/stu_col_exe";
    }

    /**
     * 学生参与课程列表
     */
    @GetMapping("/user/stu/crs")
    public String joinCrsPage(HttpServletRequest request,Model model){
        long userId=RequestUtils.getUserIdFromReq(request);
        List<Course> courses=crsSer.getJoinCrsList(userId);
        model.addAttribute("crs",courses);
        return "mgr/stu_join_crs";
    }
}
