--
-- 6.1 Subprogram Declarations
--
-- NOTE: This module is not compilation is used only for testing purposes
--

package Subprogram_Declarations is

    -- Examples of subprogram declarations:
    procedure Traverse_Tree;
    procedure Increment(X : in out Integer);
    procedure Right_Indent(Margin : out Line_Size);         -- see 3.5.4
    procedure Switch(From, To : in out Link);               -- see 3.10.1

    function Random return Probability;                     -- see 3.5.7
    function Min_Cell(X : Link) return Cell;                -- see 3.10.1
    function Next_Frame(K : Positive) return Frame;         -- see 3.10
    function Dot_Product(Left, Right : Vector) return Real; -- see 3.6
    function "*"(Left, Right : Matrix) return Matrix;       -- see 3.6

    -- Examples of in parameters with default expressions:
    procedure Print_Header
        (Pages  : in Natural;
         Center : in Boolean := True);

end Subprogram_Declarations;