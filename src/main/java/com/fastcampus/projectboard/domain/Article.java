package com.fastcampus.projectboard.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;


import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Setter @ManyToOne(optional = false) @JoinColumn(name="userId") private UserAccount userAccount;
    @Setter @Column(nullable = false) private String title;
    @Setter @Column(nullable = false, length = 10000) private String content;

    @ToString.Exclude
    @JoinTable(
            name="article_hashtag",
            joinColumns=@JoinColumn(name="articleId"),
            inverseJoinColumns=@JoinColumn(name="hashtagId")
    )
    @ManyToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE})
    private Set<Hashtag> hashtags = new LinkedHashSet<>();

    // 양방향 바인딩...댓글들을 중복 없이 컬렉션으로 보겠다!
    @ToString.Exclude // 순환참조를 막기 위함.
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL) // 맵드바이 안하면 두 테이블의 이름을 합친 새로운 테이블을 만들어 버림..
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();




    protected Article() {
    }

    private Article(UserAccount userAccount, String title, String content) {
        this.userAccount = userAccount;
        this.title = title;
        this.content = content;

    }
    public static Article of(UserAccount userAccount, String title, String content) {
        return new Article(userAccount, title, content);
    }
    public void addHashtag(Hashtag hashtag) {
        this.getHashtags().add(hashtag);
    }
    public void addHashtags(Collection<Hashtag> hashtags) {
        this.getHashtags().addAll(hashtags);
    }
    public void clearHashtags() {
        this.getHashtags().clear();
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Article article)) return false;
        return this.getId() != null && this.getId().equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
