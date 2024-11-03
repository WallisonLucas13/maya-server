package com.example.ia.mayaAI.repositories;

import com.example.ia.mayaAI.models.MessageModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<MessageModel, UUID> {
}
