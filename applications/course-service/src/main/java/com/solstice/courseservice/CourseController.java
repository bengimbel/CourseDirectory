package com.solstice.courseservice;

import org.springframework.web.bind.annotation.*;

import javax.ws.rs.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseRepository courseRepository;
    private final CoursePresenter coursePresenter;
    private final EnrollmentRepository enrollmentRepository;


    public CourseController(CourseRepository courseRepository, CoursePresenter coursePresenter, EnrollmentRepository enrollmentRepository) {
        this.courseRepository = courseRepository;
        this.coursePresenter = coursePresenter;
        this.enrollmentRepository = enrollmentRepository;
    }

    @GetMapping("/greeting")
    public String greeting() {
        return "Course Service";
    }

    @GetMapping
    public List<CourseInfo> courses() {
        return courseRepository.getAll()
                .stream()
                .map(coursePresenter::present)
                .collect(toList());
    }

    @GetMapping("tag/{tagName}")
    public List<CourseInfo> coursesByTag(@PathVariable("tagName") String tag) {
        return courseRepository.getAllByTag(tag)
                .stream()
                .map(coursePresenter::present)
                .collect(toList());
    }

    @GetMapping("employeeId/tag/{tagName}")
    public List<String> courseIdByTag(@PathVariable("tagName") String tag) {
        List<CourseInfo> courseInfo = this.coursesByTag(tag);
        List<String> ids = courseInfo.stream().map(info -> info.id).collect(toList());
        if (ids.size() == 0) {
            return ids;
        }

        return this.employeeIdsByCourseIds(ids);
    }

    private List<CourseInfo> coursesByIds(List<String> ids) {
        return courseRepository.getCoursesByIds(ids)
                .stream()
                .map(coursePresenter::present)
                .collect(toList());
    }

    @GetMapping("employee/{id}")
    public List<CourseInfo> completedCoursesByEmployeeId(@PathVariable("id") String id) {
        List<String> courseIds = enrollmentRepository.getCompletedCourseIdsForEmployee(id);
        if (courseIds.size() == 0)
            return null;

        return this.coursesByIds(courseIds);
    }

    private List<String> employeeIdsByCourseIds(List<String> courseIds) {
        List<String> employeeIds = enrollmentRepository.getEmployeeIdsByCourseIds(courseIds);
        if (employeeIds.size() == 0)
            return employeeIds;

        return employeeIds;
    }

}