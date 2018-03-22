package me.anqi.jexam.utils;

import me.anqi.jexam.entity.Exercise;
import me.anqi.jexam.entity.Lesson;
import me.anqi.jexam.entity.auxiliary.ExeForm;

import java.util.*;

public class JmoocBeanUtils {

    public static Set<Lesson> setFileAndExeNum(Set<Lesson> lessons, long crsId){

        for (Lesson lesson:lessons){
            if (lesson.getExerciseList()==null){
                lesson.setExeNum(0);
            }else {
                lesson.setExeNum(lesson.getExerciseList().size());
            }
            lesson.setFileUrl(FileType.FILE.getUrl()+crsId+"/"+lesson.getId());
        }
        return lessons;
    }


    public static Exercise exeFormToBean(ExeForm form, long lesId){
        Exercise exercise=new Exercise(
                form.getTitle(),
                form.getDifficulty(),
                form.getAnswer(),
                1,
                form.getAnalysis(),
                form.getType()
        );
        Map<Character,String> chooseMap=new HashMap<>();
        chooseMap.put('A',form.getA());
        chooseMap.put('B',form.getB());

        if (form.getC()!=null && !form.getC().trim().isEmpty()){
            chooseMap.put('C',form.getD());
        }
        if (form.getD()!=null && !form.getD().trim().isEmpty()){
            chooseMap.put('D',form.getD());
        }

        exercise.setChooses(JsonUtils.instance.toJson(chooseMap));
        exercise.setContent(form.getContent());
        Lesson lesson=new Lesson(lesId);
        exercise.setLesson(lesson);
        return exercise;
    }

    public static Collection<Exercise> setExeChooseList(Collection<Exercise> exercises){
        for (Exercise exe:exercises){
             Map<Character,String> chos=JsonUtils.instance.readJsonToExeMap(exe.getChooses());
             exe.setChooseList(chos);
        }
        return exercises;
    }

    public static void setOneExeChooseList(Exercise exe){
        Map<Character,String> chos=JsonUtils.instance.readJsonToExeMap(exe.getChooses());
        exe.setChooseList(chos);
    }

    public static String getDataFromJsonMap(String result){
        HashMap<String,String> jsonMap=JsonUtils.instance.readJsonToStrMap(result);
        if (jsonMap.isEmpty()){
            return "";
        }else {
            return jsonMap.get("1");
        }
    }
}
