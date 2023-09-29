import React, {useEffect, useState} from 'react';
import {Text, View, NativeEventEmitter, NativeModules} from 'react-native';
const {VolumeEmitter, ScanEmitter} = NativeModules;

const EventComponent = () => {
  const [eventData, setEventData] = useState('null');

  useEffect(() => {
    ScanEmitter.generateProfile();

    const volSubscription = new NativeEventEmitter(VolumeEmitter).addListener(
      'VolumeChangeEvent',
      event => {
        setEventData(event);
      },
    );

    const scanSubscription = new NativeEventEmitter(ScanEmitter).addListener(
      'scanEvent',
      event => {
        setEventData(event);
      },
    );

    return () => {
      volSubscription.remove();
      scanSubscription.remove();
    };
  }, []);

  return (
    <View>
      <Text>Event Data:</Text>
      <Text>{eventData}</Text>
    </View>
  );
};

export default EventComponent;
