package dat.dto;

import dat.entities.Poem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PoemDTO
{
    private Long id;
    private String title;
    private String author;
    private String content;

    public PoemDTO(Poem poem)
    {
        this.id = poem.getId();
        this.title = poem.getTitle();
        this.author = poem.getAuthor();
        this.content = poem.getContent();
    }
}
