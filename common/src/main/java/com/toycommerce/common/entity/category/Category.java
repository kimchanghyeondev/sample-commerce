package com.toycommerce.common.entity.category;

import com.toycommerce.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(nullable = false)
    private Boolean enabled;

    // 자기참조: 부모 카테고리
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 자기참조: 자식 카테고리들
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Category> children = new ArrayList<>();

    // 뎁스 계산을 위한 헬퍼 메서드
    public int getDepth() {
        int depth = 0;
        Category current = this.parent;
        while (current != null) {
            depth++;
            current = current.getParent();
        }
        return depth;
    }

    // 루트 카테고리인지 확인
    public boolean isRoot() {
        return this.parent == null;
    }

    // 리프 카테고리인지 확인
    public boolean isLeaf() {
        return this.children == null || this.children.isEmpty();
    }

    // 자식 카테고리 추가
    public void addChild(Category child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
        child.setParent(this);
    }

    // 자식 카테고리 제거
    public void removeChild(Category child) {
        if (this.children != null) {
            this.children.remove(child);
            child.setParent(null);
        }
    }

    // 부모 설정
    public void setParent(Category parent) {
        this.parent = parent;
    }
}
