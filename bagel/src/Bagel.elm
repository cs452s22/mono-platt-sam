module Bagel exposing (..)

-- For Api.Object.Token scope
import Api.Enum.TokenType exposing (..)
import Api.Object exposing (Token)
import Api.Object.Token as TokenFields
import Api.Query as Query
import Browser
import Graphql.Http
import Graphql.Operation exposing (RootQuery)
import Graphql.SelectionSet as SelectionSet exposing (SelectionSet)
import Html exposing (Html, Attribute, div, input, button, text, pre)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import RemoteData exposing (RemoteData)

-- Main

type alias Response =
    List Token

type alias Token =
    { type_: TokenType
    , lexeme: String
    , line: Int
    }

type Msg
    = GotResponse (RemoteData (Graphql.Http.Error Response) Response)
    | ChangeText String
    | Scan

type alias Model =
    { filter : String
    , tokens : RemoteData (Graphql.Http.Error Response) Response
    }

query : Model -> SelectionSet Response RootQuery
query model =
    Query.tokens { code = model.filter } tokenInfoSelection


tokenInfoSelection : SelectionSet Token Api.Object.Token
tokenInfoSelection =
    SelectionSet.map3 Token
        TokenFields.type_
        TokenFields.lexeme
        TokenFields.line

makeRequest : Model -> Cmd Msg
makeRequest model =
    query model
        |> Graphql.Http.queryRequest "http://localhost:8080/graphql"
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)

type alias Flags =
    ()

init : Flags -> ( Model, Cmd Msg )
init _ =
    ( { tokens = RemoteData.NotAsked, filter = "" }, Cmd.none )

update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GotResponse response ->
            ( { model | tokens = response }, Cmd.none )

        ChangeText s ->
            ( { model | filter = s }, Cmd.none )

        Scan ->
            ( model, makeRequest model )

main =
    Browser.element
        { init = init
        , view = view
        , update = update
        , subscriptions = subscriptions
        }

subscriptions : Model -> Sub Msg
subscriptions model =
    Sub.none


view : Model -> Html Msg
view model =
    div []
        [ input [ value model.filter, onInput ChangeText ] []
        , div []
            [ button [ onClick Scan ] [ text "Get Tokens" ]
            ]
        , div []
            [ viewResponse model.tokens
            ]
        ]


viewResponse model =
    case model of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            text "loading"

        RemoteData.Success response ->
            text (Debug.toString response)

        RemoteData.Failure httpError ->
            text ("Error: " ++ Debug.toString httpError)