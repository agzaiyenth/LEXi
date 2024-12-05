import Burnt  from 'burnt';

export const showToast = (message: string, type: 'success' | 'error' | 'info' = 'info') => {
  switch (type) {
    case 'success':
      Burnt.success({
        title: 'Success',
        message,
      });
      break;
    case 'error':
      Burnt.danger({
        title: 'Error',
        message,
      });
      break;
    case 'info':
    default:
      Burnt.info({
        title: 'Info',
        message,
      });
      break;
      Burnt.toast({
        message: message,
        type: type,
        position: 'top-right',
        duration: 5000
      });Burnt.dismissAllAlerts();
  }
};
