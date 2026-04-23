/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.smart.campus.api.exception;

/**
 *
 * @author VICTUS
 */
import com.mycompany.smart.campus.api.model.ErrorMessage;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class GlobalExceptionMapper implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(Throwable exception) {
        ErrorMessage msg = new ErrorMessage("An unexpected internal server error occurred.", 500);
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(msg).build();
    }
}
