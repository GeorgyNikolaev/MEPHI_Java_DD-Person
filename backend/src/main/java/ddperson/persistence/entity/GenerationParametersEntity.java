package ddperson.persistence.entity;

import ddperson.domain.enums.Mood;
import ddperson.domain.enums.RoleArchetype;
import ddperson.domain.enums.UniverseStyle;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "generation_parameters")
public class GenerationParametersEntity {

    @Id
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "request_id")
    private GenerationRequestEntity request;

    @Column(name = "character_description", columnDefinition = "TEXT", nullable = false)
    private String characterDescription;

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

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GenerationRequestEntity getRequest() {
        return request;
    }

    public void setRequest(GenerationRequestEntity request) {
        this.request = request;
    }

    public String getCharacterDescription() {
        return characterDescription;
    }

    public void setCharacterDescription(String characterDescription) {
        this.characterDescription = characterDescription;
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
}
