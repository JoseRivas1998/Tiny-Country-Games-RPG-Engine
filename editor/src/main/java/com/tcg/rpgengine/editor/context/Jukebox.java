package com.tcg.rpgengine.editor.context;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.badlogic.gdx.backends.lwjgl.audio.LwjglAudio;
import com.badlogic.gdx.backends.lwjgl.audio.OpenALLwjglAudio;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Disposable;
import com.tcg.rpgengine.common.data.assets.SoundAsset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

public class Jukebox implements Disposable {

    private final LwjglAudio audio;
    private final Map<UUID, Music> music;
    private final Map<UUID, Sound> sound;
    private final AtomicBoolean shouldUpdate = new AtomicBoolean(true);
    private final Semaphore audioSemaphore = new Semaphore(1);

    public Jukebox() {
        LwjglNativesLoader.load();
        this.audio = new OpenALLwjglAudio();
        this.music = new HashMap<>();
        this.sound = new HashMap<>();
        new Thread(() -> {
            while (this.shouldUpdate.get()) {
                this.audioSync(this.audio::update);
            }
            this.audio.dispose();
        }).start();
    }

    public void playMusic(SoundAsset music, float volume) {
        this.validateVolume(volume);
        this.audioSync(() -> {
            final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
            Music song;
            if (this.music.containsKey(music.id)) {
                song = this.music.get(music.id);
            } else {
                song = this.audio.newMusic(projectFileHandle.sibling(music.path));
                this.music.put(music.id, song);
            }
            song.stop();
            song.setLooping(true);
            song.setVolume(volume);
            song.play();
        });
    }

    public void playSoundEffect(SoundAsset soundAsset, float volume) {
        this.validateVolume(volume);
        this.audioSync(() -> {
            final FileHandle projectFileHandle = ApplicationContext.context().currentProject.getProjectFileHandle();
            Sound soundEffect;
            if (this.sound.containsKey(soundAsset.id)) {
                soundEffect = this.sound.get(soundAsset.id);
            } else {
                soundEffect = this.audio.newSound(projectFileHandle.sibling(soundAsset.path));
                this.sound.put(soundAsset.id, soundEffect);
            }
            soundEffect.stop();
            soundEffect.play(volume);
        });
    }

    private void validateVolume(float volume) {
        if (Float.compare(volume, 0f) < 0 || Float.compare(volume, 1f) > 0) {
            throw new IllegalArgumentException("Volume must be on interval [0.0, 1.0]");
        }
    }

    public void stopAll() {
        this.audioSync(() -> {
            this.music.values().forEach(Music::stop);
            this.sound.values().forEach(Sound::stop);
        });
    }

    private void audioSync(Runnable runnable) {
        try {
            this.audioSemaphore.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runnable.run();
        this.audioSemaphore.release();
    }

    public void disposeAll(Collection<? extends Disposable> disposables) {
        disposables.forEach(Disposable::dispose);
    }

    @Override
    public void dispose() {
        this.disposeAll(this.music.values());
        this.music.clear();
        this.disposeAll(this.sound.values());
        this.sound.clear();
        this.shouldUpdate.set(false);
    }
}
