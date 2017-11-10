import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.CannotWriteException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.audio.mp3.MP3File;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by Caitlin on 11/9/2017.
 */
public class Music_Sorter_3000
{
    public File musicFile;
    public File[] artists;

    public Music_Sorter_3000()
    {
        try
        {
            musicFile = new File("C:\\Users\\Caitlin\\IdeaProjects\\MusicSorter3000\\.idea\\src\\tunes");
        }
        catch(Exception ex)
        {
            System.out.println("bad file");
        }
        artists = musicFile.listFiles();
    }

    public static void main(String[] args)
    {
        Music_Sorter_3000 sorter3000 = new Music_Sorter_3000();
        for (File artist : sorter3000.artists)
        {
            File[] albums = artist.listFiles();
            for (File album : albums)
            {
                String artistName = artist.getName();
                String albumName = album.getName();
                File[] songs = album.listFiles();
                for (File songFile : songs)
                {
                    try
                    {

                        AudioFile song = AudioFileIO.read(songFile);
                        Tag tag = song.getTag();

                        tag.setField(FieldKey.ALBUM, albumName);
                        tag.setField(FieldKey.ARTIST, artistName);
                        tag.setField(FieldKey.COMMENT, "");
                        song.commit();

                        String title = tag.getFirst(FieldKey.TITLE);
                        String path = album.getPath();

                        File temp = new File(path + "\\" + title + ".mp3");
                        songFile.renameTo(temp);

                        System.out.println("Song: " + songFile.getName() + " done!");
                    }
                    catch( CannotReadException | CannotWriteException | IOException | InvalidAudioFrameException
                                | KeyNotFoundException | TagException | ReadOnlyFileException ex)
                    {
                        System.out.println("BAD SONG NO HOW DARE YOU");
                    }
                }
            }
        }
    }
}
