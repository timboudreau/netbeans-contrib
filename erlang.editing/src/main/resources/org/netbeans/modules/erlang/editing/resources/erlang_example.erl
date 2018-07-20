%% Comment example
%% @doc Comment doc tag example 
-module(erlang_example).
-define(Macro, macro).
-record(rec, {field1, field2}).
-export([fun_example/1]).

fun_example(Term) when is_list(Term) ->
    Str = "String", % line comment
    Char = $/, Integer = 123 + Char, Float = 123.1,
    Record = #rec{field1 = Float, field2 = b},
    atom = list_to_atom(Term),
    ?Macro.

