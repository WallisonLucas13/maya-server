package com.example.ia.mayaAI.repositories;

import com.example.ia.mayaAI.models.ConversationModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<ConversationModel, UUID> {

    List<ConversationModel> findAllByUsername(String username);
}
