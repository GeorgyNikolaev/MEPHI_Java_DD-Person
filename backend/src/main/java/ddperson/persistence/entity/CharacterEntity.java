package ddperson.persistence.entity;

import ddperson.domain.enums.Mood;
import ddperson.domain.enums.RoleArchetype;
import ddperson.domain.enums.UniverseStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "characters")
public class CharacterEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_archetype", nullable = false, length = 50)
    private RoleArchetype roleArchetype;

    @Enumerated(EnumType.STRING)
    @Column(name = "universe_style", nullable = false, length = 50)
    private UniverseStyle universeStyle;

    @Column(name = "seriousness_level", nullable = false)
    private short seriousnessLevel;

    @Column(name = "expressiveness_level", nullable = false)
    private short expressivenessLevel;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Mood mood;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_portrait_id")
    private PortraitEntity lastPortrait;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RoleArchetype getRoleArchetype() {
        return roleArchetype;
    }

    public void setRoleArchetype(RoleArchetype roleArchetype) {
        this.roleArchetype = roleArchetype;
    }

    public UniverseStyle getUniverseStyle() {
        return universeStyle;
    }

    public void setUniverseStyle(UniverseStyle universeStyle) {
        this.universeStyle = universeStyle;
    }

    public short getSeriousnessLevel() {
        return seriousnessLevel;
    }

    public void setSeriousnessLevel(short seriousnessLevel) {
        this.seriousnessLevel = seriousnessLevel;
    }

    public short getExpressivenessLevel() {
        return expressivenessLevel;
    }

    public void setExpressivenessLevel(short expressivenessLevel) {
        this.expressivenessLevel = expressivenessLevel;
    }

    public Mood getMood() {
        return mood;
    }

    public void setMood(Mood mood) {
        this.mood = mood;
    }

    public PortraitEntity getLastPortrait() {
        return lastPortrait;
    }

    public void setLastPortrait(PortraitEntity lastPortrait) {
        this.lastPortrait = lastPortrait;
    }
}
