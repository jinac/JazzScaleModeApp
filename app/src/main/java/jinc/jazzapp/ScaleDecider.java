package jinc.jazzapp;

import java.util.Arrays;

/**
 * Created by jinc on 4/2/2015.
 */
public class ScaleDecider {
    private static String[] SHARP_ARRAY = {"C","C#","D","D#","E","F",
                                           "F#","G","G#","A","A#","B"};
    private static String[] FLAT_ARRAY = {"C","Db","D","Eb","E","F",
                                          "Gb","G","Ab","A","Bb","B"};

    public enum Accidental {
        FLAT,
        NATURAL,
        SHARP
    }

    public enum ScaleType {
        MAJOR,
        DOMINANT,
        MINOR,
        HALF_DIMINISHED
    }

    // Major modes
    private static int[] lydian = {0,2,4,6,7,9,11};
    private static int[] ionian = {0,2,4,5,7,9,11};

    // Dominant modes
    private static int[] mixolydian = {0,2,4,5,7,9,10};
    private static int[] lydian_b7 = {0,2,4,6,7,9,10};
    private static int[] altered = {0,1,3,4,6,7,8,10};
    private static int[] sym_dim = {0,1,3,4,6,7,9,10};

    // Minor modes
    private static int[] dorian = {0,2,3,5,7,9,10};
    private static int[] aeolian = {0,2,3,5,7,8,10};
    private static int[] phrygian = {0,1,3,5,7,8,10};
    private static int[] locrian = {0,1,3,5,6,8,10};

    // public variables
    public int key_home;
    public Accidental key_acc;
    public int[] scale_notes;

    public ScaleDecider() {
        reset();
    }

    public void reset() {
        key_home = -1;
        key_acc = Accidental.NATURAL;
    }

    public String[] get_scale(char key,
                           Accidental accidental,
                           ScaleType scale_type,
                           int[] melody_notes) {
        // get current key offset
        int key_home_offset = get_key_home_interval(key, accidental);

        // convert melody notes to scale relative notes
        int[] scale_relatives = translate_intervals(key_home_offset, melody_notes);

        // figure out proper scale and return notes of correct scale mode
        int[] scale_intervals;
        switch (scale_type) {
            case MAJOR:
                if (key_home > -1) { // append last notes if we have a previous chord
                    int prev_key_offset = scale_notes[0] - key_home_offset;
                    int[] prev_scale_translated = translate_intervals(prev_key_offset, scale_notes);
                    int[] old_note_degrees = scale_relatives;
                    scale_relatives = new int[old_note_degrees.length+prev_scale_translated.length];
                    System.arraycopy(old_note_degrees, 0,
                                     scale_relatives, 0,
                                     old_note_degrees.length);
                    System.arraycopy(prev_scale_translated, 0,
                                     scale_relatives, old_note_degrees.length,
                                     prev_scale_translated.length);
                }
                scale_intervals = this.determine_major(scale_relatives);
                break;
            case DOMINANT:
                scale_intervals = this.determine_dominant(scale_relatives);
                break;
            case MINOR:
                scale_intervals = this.determine_minor(scale_relatives);
                break;
            case HALF_DIMINISHED:
                scale_intervals = locrian;
                break;
            default:
                scale_intervals = scale_relatives;
                break;
        }

        key_home = key;
        key_acc = accidental;

        // translate relative intervals to true notes & return
        scale_notes = translate_intervals(key_home_offset, scale_intervals);
        return translate_music_alpha(accidental, scale_notes);
    }

    public String[] translate_music_alpha(Accidental accidental,int[] note_ints) {
        String[] notes = new String[note_ints.length];
        String[] note_list = new String[0];
        switch (accidental) {
            case SHARP:
                note_list = SHARP_ARRAY;
                break;
            case NATURAL:
            case FLAT:
                note_list = FLAT_ARRAY;
                break;
        }

        for (int i=0; i<note_ints.length; i++) {
            notes[i] = note_list[note_ints[i] % 12];
        }
        return notes;
    }

    public int get_key_home_interval(char key, Accidental accidental) {
        String key_element = String.valueOf(key);
        int key_index;
        switch (accidental) {
            case SHARP: // add sharp sign
                key_element += '#';
                key_index = Arrays.asList(SHARP_ARRAY).indexOf(key_element);
                break;

            case FLAT: // add flat sign
                key_element += 'b';

            case NATURAL: // Don't add accidental
            default:  // Piggyback natural onto flat source
                key_index = Arrays.asList(FLAT_ARRAY).indexOf(key_element);
                break;
        }

        return key_index;
    }

    public int[] translate_intervals(int key_offset, int[] scale_intervals) {
        int[] scale = new int[scale_intervals.length];
        for (int i=0; i<scale_intervals.length; i++) {
            scale[i] = scale_intervals[i] + key_offset;
        }
        return scale;
    }

    public int[] determine_major(int[] intervals) {
        // Look for 6 interval
        boolean sharp_four = false;
        for (int i=0; i > intervals.length; i++) {
            if ((intervals[i] % 12) == 6) {
                sharp_four = true;
            }
        }

        int[] scale_intervals;
        if (sharp_four) { // LYDIAN
            scale_intervals = lydian;
        } else { // IONIAN
            scale_intervals = ionian;
        }
        return scale_intervals;
    }

    public int[] determine_dominant(int[] intervals) {
        // Look for 6 interval
        boolean sharp_four = false;
        boolean flat_six = false;
        boolean altered_nine = false;
        for (int i=0; i > intervals.length; i++) {
            if ((intervals[i] % 12) == 6) {
                sharp_four = true;
            }
            if (((intervals[i] % 12) == 1) ||
                    (intervals[i] == 3)) {
                altered_nine = true;
            }
            if ((intervals[i] % 12) == 8) {
                flat_six = true;
            }
        }

        int[] scale_intervals;
        if (sharp_four) {
            if (altered_nine) { //ALTERED
                scale_intervals = altered;
            } else { //LYDIAN_b7
                scale_intervals = lydian_b7;
            }
        } else if (flat_six) { // SYMMETRIC_DIMINISHED
            scale_intervals = sym_dim;
        } else { // MIXOLYDIAN
            scale_intervals = mixolydian;
        }
        return scale_intervals;
    }

    public int[] determine_minor(int[] intervals) {
        // Look for 6 interval
        boolean flat_six = false;
        boolean flat_nine = false;
        for (int i=0; i > intervals.length; i++) {
            if ((intervals[i] % 12) == 8) {
                flat_six = true;
            }
            if ((intervals[i] % 12) == 1) {
                flat_nine = true;
            }
        }
        int[] scale_intervals;
        if (flat_six) {
            if (flat_nine) {
                scale_intervals = phrygian;
            } else {
                scale_intervals = aeolian;
            }
        } else {
            scale_intervals = dorian;
        }
        return scale_intervals;
    }
}
