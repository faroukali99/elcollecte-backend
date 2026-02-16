// CreateFormulaireRequest.java
package com.elcollecte.formulaire.dto;

import jakarta.validation.constraints.*;
import java.util.List;

public record CreateFormulaireRequest(
        @NotNull Long          projetId,
        @NotBlank @Size(max=200) String titre,
        String                  description,
        List<Object>            schemaQuestions
) {}