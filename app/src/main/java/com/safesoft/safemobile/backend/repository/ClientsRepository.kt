package com.safesoft.safemobile.backend.repository

import com.safesoft.safemobile.backend.api.service.ClientService
import com.safesoft.safemobile.backend.db.local.dao.ClientsDao
import com.safesoft.safemobile.backend.db.local.entity.Clients
import javax.inject.Inject

class ClientsRepository @Inject constructor(
    private val clientsDao: ClientsDao,
    private val clientService: ClientService
) {

    fun getAllClients() = clientsDao.getAllClients()

    fun getClientById(id: Long) = clientsDao.getClientById(id)

    fun searchClient(query: String) = clientsDao.searchClient(query)

    fun searchClientFlow(query: String) = clientsDao.searchClientFlow(query)

    fun addClients(vararg clients: Clients) = clientsDao.addClients(*clients)

    fun updateClients(vararg clients: Clients) = clientsDao.updateClients(*clients)

    fun deleteClients(vararg clients: Clients) = clientsDao.deleteClients(*clients)

    fun deleteAllClients() = clientsDao.deleteAllClients()

    fun loadClientsFromServer() = clientService.getAllClients()


}