--
-- 3.3.2 Number Declarations
--
-- number_declaration ::=
--   defining_identifier_list : constant := static_expression;
--
-- NOTE: This module is not compilation is used only for testing purposes
--

procedure Number_Declaratios is

    -- Examples of number declarations:
    Two_Pi          : constant := 2.0*Ada.Numerics.Pi;  -- a real number (see A.5)
    Max             : constant := 500;                  -- an integer number
    Max_Line_Size   : constant := Max/6;                -- the integer 83
    Power_16        : constant := 2**16;                -- the integer 65_536
    One, Un, Eins   : constant := 1;                    -- three different names for 1

begin

    null;

end Number_Declaratios;