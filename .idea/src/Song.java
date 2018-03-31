import com.wrapper.spotify.Api;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.Request;
import com.wrapper.spotify.models.*;
import org.apache.commons.lang.ObjectUtils;
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

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caitlin on 3/31/2018.
 */
public class Song
{
    public Tag tag;
    public String title;
    public String artist;
    public String album;
    public AudioFile songFile;

    public Song(File file)
    {
        try
        {
            this.songFile = AudioFileIO.read(file);
            this.tag = songFile.getTag();
            this.title = songFile.getFile().getName().replace(".mp3", "");
            this.album = "";
            this.artist = "";
        }
        catch(Exception ex)
        {
            System.out.println("Song.java: bad song file read. "+ ex.getMessage());
        }
    }

    public void saveTag(String title, String artist, String album)
    {
        try
        {
            tag.setField(FieldKey.TITLE, title);
            tag.setField(FieldKey.ALBUM, album);
            tag.setField(FieldKey.ARTIST, artist);
            tag.setField(FieldKey.COMMENT, "");
            songFile.commit();
        }
        catch (Exception ex)
        {
            System.out.println("Song.java: failed to save song tags "+ ex.getMessage());
        }
    }

    public void saveCleanInfo(String title, String album, String artist)
    {
        this.title = title;
        this.album = album;
        this.artist = artist;
    }
}
