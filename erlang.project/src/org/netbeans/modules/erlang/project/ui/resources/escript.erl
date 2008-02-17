#!/usr/bin/env escript

main([String]) ->
    try
        todo
    catch
        _:_ ->
            usage()
    end;
main(_) ->
    usage().
        
usage() ->
    io:format("usage: \n"),
    halt(1).
