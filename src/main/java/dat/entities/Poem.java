package dat.entities;

import dat.dto.PoemDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class Poem
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String author;
    private String content;


    public Poem(String title)
    {
        this.title = title;
    }

    public Poem(String title, String author, String content)
    {
        this.title = title;
        this.author = author;
        this.content = content;
    }

    public Poem(PoemDTO poemDTO)
    {
        this.title = poemDTO.getTitle();
        this.author = poemDTO.getAuthor();
        this.content = poemDTO.getContent();
    }
}
