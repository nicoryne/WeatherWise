package com.example.weatherwise.server;

public class Server extends HTTPClient {

    private final String DEBUG_TAG = "Server";


    public Server() {
        super("jdbc:postgresql://aws-0-ap-southeast-1.pooler.supabase.com:5432/postgres?user=postgres.xsukthsmskhfmvjtjhbj&password=H76gVs4dp5PhMYKA9WceBt");
    }

    @Override
    public HTTPClientService getHttpClientService() {
        return super.getHttpClientService();
    }


}
