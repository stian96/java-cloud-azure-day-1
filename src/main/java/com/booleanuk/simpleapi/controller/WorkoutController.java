
package com.booleanuk.simpleapi.controller;

import com.booleanuk.simpleapi.model.Workout;
import com.booleanuk.simpleapi.repository.WorkoutRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("v1/workouts")
public class WorkoutController {
    private final WorkoutRepository workoutRepository;

    @Autowired
    public WorkoutController(WorkoutRepository workoutRepository) {
        this.workoutRepository = workoutRepository;
    }

    @GetMapping
    public ResponseEntity<List<Workout>> getAllWorkouts() {
        return ResponseEntity.ok(this.workoutRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Workout> getWorkoutById(@PathVariable int id) {
        return this.workoutRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Workout> createWorkout(@RequestBody Workout workout) {
        workout.getExercises().forEach(exercise -> exercise.setWorkout(workout));
        Workout savedWorkout = this.workoutRepository.save(workout);
        return ResponseEntity.ok(savedWorkout);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Workout> updateWorkout(@PathVariable int id, @RequestBody Workout workoutBody) {
        return this.workoutRepository.findById(id)
                .map(existingWorkout -> {
                    existingWorkout.setWorkoutType(workoutBody.getWorkoutType());
                    existingWorkout.setWorkoutDate(workoutBody.getWorkoutDate());

                    existingWorkout.getExercises().clear();
                    workoutBody.getExercises().forEach(exercise -> {
                        exercise.setWorkout(existingWorkout);
                        existingWorkout.getExercises().add(exercise);
                    });

                    Workout updatedWorkout = this.workoutRepository.save(existingWorkout);
                    return ResponseEntity.ok(updatedWorkout);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteWorkout(@PathVariable int id) {
        return this.workoutRepository.findById(id)
                .map(workout -> {
                    this.workoutRepository.delete(workout);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
