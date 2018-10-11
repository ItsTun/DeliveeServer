package com.example.tunhanmyae.DeliveeServer.model;

import java.util.List;

public class MyResponse {
    public long multicast_id;
    public int success;
    public int failure;
    public int canonicla_ids;
    public List<Result> results;
}
