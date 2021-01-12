package com.tcg.rpgengine.editor.concurrency;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskSequence extends Service<Void> {

    private final AtomicBoolean hasStarted = new AtomicBoolean(false);
    private final List<Pair<String, Runnable>> tasks = new ArrayList<>();

    public void addTask(String message, Runnable task) {
        if (this.hasStarted.get()) {
            throw new IllegalStateException("This task sequence has already begun.");
        }
        this.tasks.add(new Pair<>(message, task));
    }

    @Override
    protected Task<Void> createTask() {
        this.hasStarted.set(true);
        final int numberOfTasks = this.tasks.size();
        return new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                this.updateProgress(0, numberOfTasks);
                for (int i = 0; i < numberOfTasks; i++) {
                    final Pair<String, Runnable> task = TaskSequence.this.tasks.get(i);
                    this.updateMessage(task.getKey());
                    task.getValue().run();
                    this.updateProgress(i + 1, numberOfTasks);
                }
                return null;
            }
        };
    }
}
