/*
 * Starter code supplied for Adelaide University COMP3011 Assignment 2.
 * Students are free to modify this file for assessment purposes.
 * 
 * Authors:
 *   1. Simon Ratcliffe, in collaboration with GPT-5.6 Terra
 *   2. Johnathan Thach 1471670
 *
 * Copyright 2026 Simon Ratcliffe
 */
package comp3011;

import java.util.Map;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created in the VideoPlayerApp's main function to receive and parse the video player's command-line arguments.
 *
 * <p>
 * Extracts the video file and supported launch options, reports invalid input
 * or help text, and exposes the resulting launch configuration via methods to the
 * {@link VideoPlayerApp}.
 * </p>
 */
public class CommandLineController {
    private final String[] args;
    private final List<FrameProcessor> frameProcessors = new ArrayList<>();

    private boolean helpRequested;
    private boolean audioRequested;
    private boolean maximiseRequested;
    private Integer displayId;
    private File videoFile;
    private String errorMessage;

    public CommandLineController(String[] args) {
        this.args = args.clone();
        parse();
        if (errorMessage != null) {
            System.out.println(errorMessage);
        }
        if (helpRequested) {
            printHelp();
        }
    }

    public File getVideoFile() {
        return videoFile;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public int getExitCode() {
        return errorMessage == null ? 0 : 1;
    }

    public Integer getDisplayId() {
        return displayId;
    }

    public boolean isAudioRequested() {
        return audioRequested;
    }

    public boolean isMaximiseRequested() {
        return maximiseRequested;
    }

    public boolean shouldLaunchApplication() {
        return errorMessage == null && videoFile != null;
    }

    public List<FrameProcessor> getFrameProcessors() {
        return frameProcessors;
    }


    
    private void parse() {
        List<String> videoFiles = new ArrayList<>();

        for (String arg : args) {
            if ("-h".equals(arg) || "--help".equals(arg)) {
                helpRequested = true;
            } else if ("-a".equals(arg) || "--audio".equals(arg)) {
                audioRequested = true;
            } else if ("-x".equals(arg) || "--maximise".equals(arg)) {
                maximiseRequested = true;
            } else if ("-1".equals(arg) || "--monitor-1".equals(arg)) {
                setDisplayId(1);
            } else if ("-2".equals(arg) || "--monitor-2".equals(arg)) {
                setDisplayId(2);

            // Long effect options
            } else if ("--bleed-frames".equals(arg)) {
                addEffect('b');
            } else if ("--scratch-frames".equals(arg)) {
                addEffect('s');
            } else if ("--flicker-frames".equals(arg)) {
                addEffect('f');
            } else if ("--black-and-white".equals(arg)) {
                addEffect('w');
            } else if ("--yellow-frames".equals(arg)) {
                addEffect('y');
            } else if ("--vignette-frames".equals(arg)) {
                addEffect('v');
            } else if ("--dust-frames".equals(arg)) {
                addEffect('d');
            } else if ("--jitter-frames".equals(arg)) {
                addEffect('j');
            } else if ("--mottle-frames".equals(arg)) {
                addEffect('m');
            } else if ("--number-frames".equals(arg)) {
                addEffect('n');
            } else if ("--pepper-frames".equals(arg)) {
                addEffect('p');

            // Stacked short effect options, such as -jfddn
            } else if (arg.length() > 2
                    && arg.startsWith("-")
                    && isStackedEffectOption(arg)) {
                for (int i = 1; i < arg.length(); i++) {
                    addEffect(arg.charAt(i));
                }

            // Single short effect options
            } else if (arg.length() == 2
                    && arg.startsWith("-")
                    && isStackedEffectOption(arg)) {
                addEffect(arg.charAt(1));

            } else if (arg.startsWith("-")) {
                errorMessage = "Unknown option: " + arg;
            } else {
                videoFiles.add(arg);
            }
        }

        if (videoFiles.size() > 1) {
            errorMessage = "Usage: VideoPlayer [options] [video-file]";
        } else if (videoFiles.size() == 1) {
            videoFile = new File(videoFiles.get(0));

            if (!videoFile.isFile()) {
                errorMessage = "File not found: " + videoFile.getPath();
                videoFile = null;
            }
        } else {
            if (!helpRequested) {
                errorMessage = "No video file specified.";
            }
        }
    }


    private boolean isStackedEffectOption(String arg) {
        for (int i = 1; i < arg.length(); i++) {
            char option = arg.charAt(i);

            if ("bsfwyvdjmnp".indexOf(option) == -1) {
                return false;
            }
        }

        return true;
    }

    private void setDisplayId(int displayId) {
        if (this.displayId != null && this.displayId != displayId) {
            errorMessage = "Only one display option can be used";
            return;
        }
        this.displayId = displayId;
    }

    private void printHelp() {
        System.out.println("Usage: VideoPlayer [options] [video-file]");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  -h, --help         Show this help message");
        System.out.println("  -a, --audio        Play audio");
        System.out.println("  -x, --maximise     Open the player maximised");
        System.out.println("  -1, --monitor-1    Open the player on display 1");
        System.out.println("  -2, --monitor-2    Open the player on display 2");
        System.out.println("  -b, --bleed-frames       Apply light bleed");
        System.out.println("  -s, --scratch-frames     Add vertical film scratches");
        System.out.println("  -f, --flicker-frames     Randomly dim frames");
        System.out.println("  -w, --black-and-white    Convert frames to black and white");
        System.out.println("  -y, --yellow-frames      Apply warmer colour temperature");
        System.out.println("  -v, --vignette-frames    Darken frame edges");
        System.out.println("  -d, --dust-frames        Add dust and hair marks");
        System.out.println("  -j, --jitter-frames      Randomly displace frames");
        System.out.println("  -m, --mottle-frames      Add cloudy emulsion mottling");
        System.out.println("  -n, --number-frames      Render frame numbers");
        System.out.println("  -p, --pepper-frames      Add dark spots and blotches");
    }

    private void addEffect(char option) {
        switch (option) {
            case 'b':
                frameProcessors.add(new FrameBleeder());
                break;

            case 's':
                frameProcessors.add(new FrameScratcher());
                break;

            case 'f':
                frameProcessors.add(new FrameFlickerer());
                break;

            case 'w':
                frameProcessors.add(new FrameBlackAndWhiter());
                break;

            case 'y':
                frameProcessors.add(new FrameYellower());
                break;

            case 'v':
                frameProcessors.add(new FrameVignetter());
                break;

            case 'd':
                frameProcessors.add(new FrameDuster());
                break;

            case 'j':
                frameProcessors.add(new FrameJitterer());
                break;

            case 'm':
                frameProcessors.add(new FrameMottler());
                break;

            case 'n':
                frameProcessors.add(new FrameNumberer());
                break;

            case 'p':
                frameProcessors.add(new FramePepperer());
                break;
        }
    }
}
