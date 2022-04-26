-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module Api.Interface.Expr exposing (..)

import Api.InputObject
import Api.Interface
import Api.Object
import Api.Scalar
import Api.ScalarCodecs
import Api.Union
import Graphql.Internal.Builder.Argument as Argument exposing (Argument)
import Graphql.Internal.Builder.Object as Object
import Graphql.Internal.Encode as Encode exposing (Value)
import Graphql.Operation exposing (RootMutation, RootQuery, RootSubscription)
import Graphql.OptionalArgument exposing (OptionalArgument(..))
import Graphql.SelectionSet exposing (FragmentSelectionSet(..), SelectionSet(..))
import Json.Decode as Decode


type alias Fragments decodesTo =
    { onBinary : SelectionSet decodesTo Api.Object.Binary
    , onUnary : SelectionSet decodesTo Api.Object.Unary
    , onLiteral : SelectionSet decodesTo Api.Object.Literal
    , onGrouping : SelectionSet decodesTo Api.Object.Grouping
    }


{-| Build an exhaustive selection of type-specific fragments.
-}
fragments :
    Fragments decodesTo
    -> SelectionSet decodesTo Api.Interface.Expr
fragments selections____ =
    Object.exhaustiveFragmentSelection
        [ Object.buildFragment "Binary" selections____.onBinary
        , Object.buildFragment "Unary" selections____.onUnary
        , Object.buildFragment "Literal" selections____.onLiteral
        , Object.buildFragment "Grouping" selections____.onGrouping
        ]


{-| Can be used to create a non-exhaustive set of fragments by using the record
update syntax to add `SelectionSet`s for the types you want to handle.
-}
maybeFragments : Fragments (Maybe decodesTo)
maybeFragments =
    { onBinary = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onUnary = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onLiteral = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onGrouping = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    }

id : SelectionSet Int Api.Interface.Expr
id =
    Object.selectionForField "Int" "id" [] Decode.int
