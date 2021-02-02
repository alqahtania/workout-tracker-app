package com.example.workouttracker.domain.util



interface EntityMapper <Entity, DomainModel>{


    fun mapFromEntity(entity : Entity) : DomainModel

    fun mapToEntity(domainModel: DomainModel) : Entity

}