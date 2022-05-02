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
import Html exposing (Html, button, div, input, text)
import Html.Attributes exposing (..)
import Html.Events exposing (onClick, onInput)
import RemoteData exposing (RemoteData)

-- Main

type alias Response =
    List Token

type alias Token =
    { type_: TokenType -- type is a reserved word
    , lexeme: String
    , line: Int
    }

type Msg
    = GotResponse (RemoteData (Graphql.Http.Error Response) Response)
    | ChangeText String
    | Scan

type alias Model =
    { code: String -- changed from filter to code for lab 3
    , tokens: RemoteData (Graphql.Http.Error Response) Response
    }

query : Model -> SelectionSet Response RootQuery
query model =
    Query.tokens { code = model.code } tokenInfoSelection -- changed from filter to code


tokenInfoSelection : SelectionSet Token Api.Object.Token
tokenInfoSelection =
    SelectionSet.map3 Token
        TokenFields.type_
        TokenFields.lexeme
        TokenFields.line

makeRequest : Model -> Cmd Msg
makeRequest model =
    query model
        |> Graphql.Http.queryRequest "http://localhost:8000/graphql"
        |> Graphql.Http.send (RemoteData.fromResult >> GotResponse)

type alias Flags =
    ()

init : Flags -> ( Model, Cmd Msg )
init _ =
    ( { tokens = RemoteData.NotAsked, code = "" }, Cmd.none ) -- changed from filter to code

update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        GotResponse response ->
            ( { model | tokens = response }, Cmd.none )

        ChangeText s ->
            ( { model | code = s }, Cmd.none ) -- changed from filter to code

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
        [ input [ value model.code, onInput ChangeText ] [] -- changed from filter to code
        , div []
            [ button [ onClick Scan ] [ text "Get Tokens" ]
            ]
        , div []
            [ viewResponse model.tokens ]
        ]

-- This function is what adds the brackets and also displays the type of the Token
addBrackets : Token -> String -- takes a Token as input, returns String
addBrackets model =
    "<" ++ Api.Enum.TokenType.toString(model.type_) ++ ">" -- convert the token type to a string and add angle brackets on both sides

viewResponse model =
    case model of
        RemoteData.NotAsked ->
            text ""

        RemoteData.Loading ->
            text "loading"

        RemoteData.Success response ->
            text (String.join " " (List.map addBrackets response)) -- changed this line to make it work

        RemoteData.Failure httpError ->
            text ("Error: " ++ Debug.toString httpError)