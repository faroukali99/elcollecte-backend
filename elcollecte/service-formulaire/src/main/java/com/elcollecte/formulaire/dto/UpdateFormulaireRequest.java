// UpdateFormulaireRequest.java
package com.elcollecte.formulaire.dto;

import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateFormulaireRequest(
        @Size(max=200) String titre,
        String         description,
        List<Object>   schemaQuestions
) {}