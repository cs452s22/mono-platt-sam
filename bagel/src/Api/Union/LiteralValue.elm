-- Do not manually edit this file, it was auto-generated by dillonkearns/elm-graphql
-- https://github.com/dillonkearns/elm-graphql


module Api.Union.LiteralValue exposing (..)

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
    { onLiteralString : SelectionSet decodesTo Api.Object.LiteralString
    , onLiteralFloat : SelectionSet decodesTo Api.Object.LiteralFloat
    , onLiteralBoolean : SelectionSet decodesTo Api.Object.LiteralBoolean
    , onLiteralNull : SelectionSet decodesTo Api.Object.LiteralNull
    , onFunction : SelectionSet decodesTo Api.Object.Function
    , onClass : SelectionSet decodesTo Api.Object.Class
    }


{-| Build up a selection for this Union by passing in a Fragments record.
-}
fragments :
    Fragments decodesTo
    -> SelectionSet decodesTo Api.Union.LiteralValue
fragments selections____ =
    Object.exhaustiveFragmentSelection
        [ Object.buildFragment "LiteralString" selections____.onLiteralString
        , Object.buildFragment "LiteralFloat" selections____.onLiteralFloat
        , Object.buildFragment "LiteralBoolean" selections____.onLiteralBoolean
        , Object.buildFragment "LiteralNull" selections____.onLiteralNull
        , Object.buildFragment "Function" selections____.onFunction
        , Object.buildFragment "Class" selections____.onClass
        ]


{-| Can be used to create a non-exhaustive set of fragments by using the record
update syntax to add `SelectionSet`s for the types you want to handle.
-}
maybeFragments : Fragments (Maybe decodesTo)
maybeFragments =
    { onLiteralString = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onLiteralFloat = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onLiteralBoolean = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onLiteralNull = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onFunction = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    , onClass = Graphql.SelectionSet.empty |> Graphql.SelectionSet.map (\_ -> Nothing)
    }
