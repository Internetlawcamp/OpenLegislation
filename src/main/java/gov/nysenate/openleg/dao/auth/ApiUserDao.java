package gov.nysenate.openleg.dao.auth;

import gov.nysenate.openleg.model.auth.ApiUser;
import org.springframework.dao.DataAccessException;

/** DAO Interface for retrieving and persisting ApiUser data */

public interface ApiUserDao
{
       public ApiUser getApiUserFromEmail (String email) throws DataAccessException;

       public ApiUser getApiUserFromKey (String apikey) throws DataAccessException;

       public String getApiKey (ApiUser user);

       public long getNumRequests(String email) throws DataAccessException;

       public long getNumRequestFromEmail(String email) throws DataAccessException;

       public void insertUser(ApiUser user) throws DataAccessException;

       public String getApiKeyFromEmail (String email) throws DataAccessException;

       public void deleteApiUser (ApiUser apiuser) throws DataAccessException;

}