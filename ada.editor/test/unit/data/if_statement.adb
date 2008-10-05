--
-- 5.3 If Statements
--
-- if_statement ::=
--   if condition then
--     sequence_of_statements
--   {elsif condition then
--     sequence_of_statements}
--   [else
--     sequence_of_statements]
--   end if;
--
-- condition ::= boolean_expression
--
-- NOTE: This module is not compilation is used only for testing purposes
--

procedure If_Statement is

begin

    if (Month = December and Day = 31) then
        Month := January;
        Day := 1;
        Year := Year + 1;
    end if;

    if Line_Too_Short then
        raise Layout_Error;
    elsif Line_Full then
        New_Line;
        Put(Item);
    else
        Put(Item);
    end if;

    if My_Car.Owner.Vehicle /= My_Car then -- see 3.10.1
        Report ("Incorrect data");
    end if;

end If_Statement;